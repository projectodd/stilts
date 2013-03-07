package org.projectodd.stilts.stomp.server.protocol;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.protocol.longpoll.HttpServerStompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.longpoll.HttpStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameEncoder;
import org.projectodd.stilts.stomp.server.ServerStompMessageFactory;
import org.projectodd.stilts.stomp.server.protocol.longpoll.ConnectionManager;
import org.projectodd.stilts.stomp.server.protocol.longpoll.ConnectionResumeHandler;
import org.projectodd.stilts.stomp.server.protocol.longpoll.HttpConnectHandler;
import org.projectodd.stilts.stomp.server.protocol.longpoll.HttpResponder;
import org.projectodd.stilts.stomp.server.protocol.longpoll.HttpSinkHandler;
import org.projectodd.stilts.stomp.server.protocol.longpoll.SinkManager;
import org.projectodd.stilts.stomp.server.protocol.resource.ResourceHandler;
import org.projectodd.stilts.stomp.server.protocol.resource.ResourceManager;
import org.projectodd.stilts.stomp.server.protocol.websockets.DisorderlyCloseHandler;
import org.projectodd.stilts.stomp.server.protocol.websockets.ServerHandshakeHandler;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class HTTPProtocolHandler extends SimpleChannelUpstreamHandler {

    public HTTPProtocolHandler(StompProvider provider, ExecutionHandler executionHandler, ConnectionManager connectionManager, SinkManager sinkManager, ResourceManager resourceManager) {
        this.provider = provider;
        this.executionHandler = executionHandler;
        this.connectionManager = connectionManager;
        this.sinkManager = sinkManager;
        this.resourceManager = resourceManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            List<Entry<String, String>> headers = httpReq.getHeaders();
            for (Entry<String, String> each : headers) {
                if (each.getKey().toLowerCase().contains( "websocket" )) {
                    switchToWebSockets( ctx );
                    ChannelUpstreamHandler handshaker = (ChannelUpstreamHandler) ctx.getPipeline().get( "websocket-handshake" );
                    ChannelHandlerContext handshakerContext = ctx.getPipeline().getContext( "websocket-handshake" );
                    handshaker.handleUpstream( handshakerContext, e );
                    return;
                }
            }

            if (httpReq.getMethod().equals( HttpMethod.POST )) {
                switchToLongPollClientToServer( ctx );
                ChannelUpstreamHandler connector = (ChannelUpstreamHandler) ctx.getPipeline().get( "longpoll-connector" );
                ChannelHandlerContext connectorContext = ctx.getPipeline().getContext( "longpoll-connector" );
                connector.handleUpstream( connectorContext, e );
                ctx.sendUpstream( e );
                return;
            }
            
            String cookieHeader = httpReq.getHeader( "Cookie" );
            if (cookieHeader != null) {
                CookieDecoder cookieDecoder = new CookieDecoder();
                Set<Cookie> cookies = cookieDecoder.decode( cookieHeader );
                for (Cookie each : cookies) {
                    if (each.getName().equals( "stomp-connection-id" )) {
                        switchToLongPollServerToClient( ctx );
                        ctx.sendUpstream( e );
                        return;
                    }
                }
            }

            switchToResourceServing( ctx );
            ctx.sendUpstream( e );
            return;
        }
        super.messageReceived( ctx, e );
    }

    protected void switchToWebSockets(ChannelHandlerContext ctx) throws NoSuchAlgorithmException {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.remove( this );
        pipeline.remove( "http-codec" );

        boolean secure = (pipeline.get( SslHandler.class ) != null);

        pipeline.addFirst( "disorderly-close", new DisorderlyCloseHandler() );
        pipeline.addLast( "http-encoder", new HttpResponseEncoder() );
        pipeline.addLast( "http-decoder", new HttpRequestDecoder() );
        pipeline.addLast( "websocket-handshake", new ServerHandshakeHandler( secure ) );

        pipeline.addLast( "stomp-frame-encoder", new WebSocketStompFrameEncoder() );
        pipeline.addLast( "stomp-frame-decoder", new WebSocketStompFrameDecoder() );

        ConnectionContext context = new DefaultConnectionContext();

        pipeline.addLast( "stomp-disorderly-close-handler", new StompDisorderlyCloseHandler( provider, context ) );

        pipeline.addLast( "stomp-server-connect", new ConnectHandler( provider, context ) );
        pipeline.addLast( "stomp-server-disconnect", new DisconnectHandler( provider, context ) );

        pipeline.addLast( "stomp-server-subscribe", new SubscribeHandler( provider, context ) );
        pipeline.addLast( "stomp-server-unsubscribe", new UnsubscribeHandler( provider, context ) );

        pipeline.addLast( "stomp-server-begin", new BeginHandler( provider, context ) );
        pipeline.addLast( "stomp-server-commit", new CommitHandler( provider, context ) );
        pipeline.addLast( "stomp-server-abort", new AbortHandler( provider, context ) );

        pipeline.addLast( "stomp-server-ack", new AckHandler( provider, context ) );
        pipeline.addLast( "stomp-server-nack", new NackHandler( provider, context ) );

        pipeline.addLast( "stomp-server-receipt", new ReceiptHandler( provider, context ) );

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder() );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( ServerStompMessageFactory.INSTANCE ) );

        if (this.executionHandler != null) {
            pipeline.addLast( "stomp-server-send-threading", this.executionHandler );
        }

        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );
    }

    protected void switchToLongPollClientToServer(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.remove( this );
        
        WrappedConnectionContext context = new WrappedConnectionContext();
        
        pipeline.addLast( "longpoll-connector", new ConnectionResumeHandler( connectionManager, context ) );
        
        pipeline.addLast( "stomp-http-decoder", new HttpServerStompFrameEncoder() );
        pipeline.addLast( "stomp-http-encoder", new HttpStompFrameDecoder() );
        pipeline.addLast( "stomp-disorderly-close-handler", new StompDisorderlyCloseHandler( provider, context ) );

        pipeline.addLast( "stomp-server-connect", new HttpConnectHandler( provider, context, sinkManager ) );
        pipeline.addLast( "stomp-server-disconnect", new DisconnectHandler( provider, context, false ) );

        pipeline.addLast( "stomp-server-subscribe", new SubscribeHandler( provider, context ) );
        pipeline.addLast( "stomp-server-unsubscribe", new UnsubscribeHandler( provider, context ) );

        pipeline.addLast( "stomp-server-begin", new BeginHandler( provider, context ) );
        pipeline.addLast( "stomp-server-commit", new CommitHandler( provider, context ) );
        pipeline.addLast( "stomp-server-abort", new AbortHandler( provider, context ) );

        pipeline.addLast( "stomp-server-ack", new AckHandler( provider, context ) );
        pipeline.addLast( "stomp-server-nack", new NackHandler( provider, context ) );

        pipeline.addLast( "stomp-server-receipt", new ReceiptHandler( provider, context ) );
        pipeline.addLast( "stomp-http-responder", new HttpResponder() );

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder() );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( ServerStompMessageFactory.INSTANCE ) );

        if (this.executionHandler != null) {
            pipeline.addLast( "stomp-server-send-threading", this.executionHandler );
        }
        
        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );

    }
    
    protected void switchToLongPollServerToClient(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.remove( this );
        
        WrappedConnectionContext context = new WrappedConnectionContext();
        
        pipeline.addLast( "longpoll-connector", new ConnectionResumeHandler( connectionManager, context ) );
        pipeline.addLast( "longpoll-sink-handler", new HttpSinkHandler( context, sinkManager ) );
    }

    protected void switchToResourceServing(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.remove( this );
        pipeline.addLast( "http-resource-handler", new ResourceHandler( this.resourceManager ) );
    }

    private static final Logger log = Logger.getLogger( "org.projectodd.stilts.stomp.server.protocol" );

    private StompProvider provider;
    private ExecutionHandler executionHandler;
    private ConnectionManager connectionManager;
    private SinkManager sinkManager;
    private ResourceManager resourceManager;

}
