package org.projectodd.stilts.stomp.server.protocol;

import java.nio.charset.Charset;
import java.util.concurrent.Executor;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.HttpServerCodec;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.protocol.StompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.StompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.server.ServerStompMessageFactory;
import org.projectodd.stilts.stomp.server.protocol.longpoll.ConnectionManager;
import org.projectodd.stilts.stomp.server.protocol.longpoll.SinkManager;
import org.projectodd.stilts.stomp.server.protocol.resource.ResourceManager;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class ProtocolDetector extends ReplayingDecoder<VoidEnum> {

    public ProtocolDetector(ConnectionManager connectionManager, SinkManager sinkManager, StompProvider provider, Executor executor, ResourceManager resourceManager) {
        this.provider = provider;
        this.executor = executor;
        this.connectionManager = connectionManager;
        this.sinkManager = sinkManager;

        if (this.executor != null) {
            this.executionHandler = new ExecutionHandler( this.executor );
        }
    }

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        int nonNewlineBytes = buffer.bytesBefore( (byte) '\n' );

        buffer.markReaderIndex();

        if (nonNewlineBytes > 0) {
            ChannelBuffer lineBuffer = buffer.readBytes( nonNewlineBytes );
            String line = lineBuffer.toString( UTF_8 );

            SslHandler sslHandler = context.getPipeline().get( SslHandler.class );

            buffer.resetReaderIndex();
            ChannelBuffer fullBuffer = buffer.readBytes( super.actualReadableBytes() );
            if (line.startsWith( "CONNECT" ) || line.startsWith( "STOMP" )) {
                switchToPureStomp( context );
            } else {
                switchToHttp( context );
            }

            // We want to restart at the entire head of the pipeline,
            // not just the next handler, since we jiggled the whole
            // thing pretty hard.
            //
            // Unless we're SSL, then we want to send upstream /after/ the SSL
            // handler, because we are re-sending the unencrypted payload.
            if (sslHandler != null) {
                ChannelHandlerContext sslHandlerContext = context.getPipeline().getContext( sslHandler );
                sslHandlerContext.sendUpstream( new UpstreamMessageEvent( context.getChannel(), fullBuffer, context.getChannel().getRemoteAddress() ) );
            } else {
                context.getPipeline().sendUpstream( new UpstreamMessageEvent( context.getChannel(), fullBuffer, context.getChannel().getRemoteAddress() ) );
            }
        }

        return null;
    }

    protected void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();

        pipeline.remove( this );
        pipeline.addLast( "http-codec", new HttpServerCodec() );
        pipeline.addLast( "http-handler", new HTTPProtocolHandler( provider, executionHandler, connectionManager, sinkManager, resourceManager ) );
    }

    protected void switchToPureStomp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.getPipeline();

        pipeline.remove( this );

        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder() );
        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder() );

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

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger( "org.projectodd.stilts.stomp.server.protocol" );
    private static final Charset UTF_8 = Charset.forName( "UTF-8" );

    private StompProvider provider;
    private Executor executor;
    private ExecutionHandler executionHandler;
    private ConnectionManager connectionManager;
    private SinkManager sinkManager;
    private ResourceManager resourceManager;
}
