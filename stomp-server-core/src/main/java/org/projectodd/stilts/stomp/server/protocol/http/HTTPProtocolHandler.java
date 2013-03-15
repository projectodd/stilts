package org.projectodd.stilts.stomp.server.protocol.http;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.protocol.longpoll.HttpStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameEncoder;
import org.projectodd.stilts.stomp.server.ServerStompMessageFactory;
import org.projectodd.stilts.stomp.server.protocol.AbortHandler;
import org.projectodd.stilts.stomp.server.protocol.AckHandler;
import org.projectodd.stilts.stomp.server.protocol.BeginHandler;
import org.projectodd.stilts.stomp.server.protocol.CommitHandler;
import org.projectodd.stilts.stomp.server.protocol.ConnectHandler;
import org.projectodd.stilts.stomp.server.protocol.ConnectionContext;
import org.projectodd.stilts.stomp.server.protocol.DefaultConnectionContext;
import org.projectodd.stilts.stomp.server.protocol.DisconnectHandler;
import org.projectodd.stilts.stomp.server.protocol.HostDecodingHandler;
import org.projectodd.stilts.stomp.server.protocol.NackHandler;
import org.projectodd.stilts.stomp.server.protocol.ReceiptHandler;
import org.projectodd.stilts.stomp.server.protocol.SendHandler;
import org.projectodd.stilts.stomp.server.protocol.StompDisorderlyCloseHandler;
import org.projectodd.stilts.stomp.server.protocol.SubscribeHandler;
import org.projectodd.stilts.stomp.server.protocol.UnsubscribeHandler;
import org.projectodd.stilts.stomp.server.protocol.WrappedConnectionContext;
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

            switchToRequestOriented( ctx );
            ChannelUpstreamHandler connector = (ChannelUpstreamHandler) ctx.getPipeline().get( "head" );
            ChannelHandlerContext connectorContext = ctx.getPipeline().getContext( "head" );
            connector.handleUpstream( connectorContext, e );
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

    protected void switchToRequestOriented(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();
        pipeline.remove( this );

        WrappedConnectionContext context = new WrappedConnectionContext();

        pipeline.addLast( "head", new SimpleChannelUpstreamHandler() );
        pipeline.addLast( "host-decoding-handling", new HostDecodingHandler() );
        pipeline.addLast( "longpoll-connector", new ConnectionResumeHandler( connectionManager, context ) );

        pipeline.addLast( "http-resource-handler", new ResourceHandler(  this.resourceManager ) );

        pipeline.addLast( "stomp-http-encoder", new HttpServerStompFrameEncoder() );
        pipeline.addLast( "stomp-http-decoder", new HttpStompFrameDecoder() );
        pipeline.addLast( "stomp-disorderly-close-handler", new StompDisorderlyCloseHandler( provider, context ) );

        pipeline.addLast( "DEBUG_A", new DebugHandler( "debug-a" ) );

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

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder() );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( ServerStompMessageFactory.INSTANCE ) );

        pipeline.addLast( "sse-sink-handler", new SSESinkHandler( context, sinkManager ) );
        pipeline.addLast( "longpoll-sink-handler", new HttpSinkHandler( context, sinkManager ) );
        pipeline.addLast( "stomp-http-responder", new HttpResponder() );

        if (this.executionHandler != null) {
            pipeline.addLast( "stomp-server-send-threading", this.executionHandler );
        }

        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );

    }

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger( "org.projectodd.stilts.stomp.server.protocol" );

    private StompProvider provider;
    private ExecutionHandler executionHandler;
    private ConnectionManager connectionManager;
    private SinkManager sinkManager;
    private ResourceManager resourceManager;

}
