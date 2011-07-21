package org.projectodd.stilts.stomp.server.protocol;

import java.nio.charset.Charset;
import java.util.concurrent.Executor;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.projectodd.stilts.stomp.protocol.StompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.StompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameEncoder;
import org.projectodd.stilts.stomp.server.ServerStompMessageFactory;
import org.projectodd.stilts.stomp.server.websockets.protocol.HandshakeHandler;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class ProtocolDetector extends ReplayingDecoder<VoidEnum> {

    public ProtocolDetector(StompProvider provider, Executor executor) {
        this.provider = provider;
        this.executor = executor;

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

            buffer.resetReaderIndex();
            ChannelBuffer fullBuffer = null;
            if (line.startsWith( "CONNECT" ) || line.startsWith( "STOMP" )) {
                fullBuffer = switchToPureStomp( context, buffer );
            } else {
                fullBuffer = switchToStompOverWebSockets( context, buffer );
            }

            context.getPipeline().sendUpstream( new UpstreamMessageEvent( context.getChannel(), fullBuffer, context.getChannel().getRemoteAddress() ) );
        }

        return null;
    }

    protected ChannelBuffer switchToPureStomp(ChannelHandlerContext context, ChannelBuffer buffer) {
        ChannelBuffer fullBuffer = buffer.readBytes( super.actualReadableBytes() );

        ChannelPipeline pipeline = context.getPipeline();

        pipeline.remove( this );
        
        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder() );
        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder() );
        
        appendCommonHandlers( pipeline );

        return fullBuffer;

    }

    protected ChannelBuffer switchToStompOverWebSockets(ChannelHandlerContext context, ChannelBuffer buffer) {
        ChannelBuffer fullBuffer = buffer.readBytes( super.actualReadableBytes() );
        ChannelPipeline pipeline = context.getPipeline();
        pipeline.remove( this );

        pipeline.addLast( "http-encoder", new HttpResponseEncoder() );
        pipeline.addLast( "http-decoder", new HttpRequestDecoder() );
        pipeline.addLast( "websocket-handshake", new HandshakeHandler() );
        
        pipeline.addLast( "stomp-frame-encoder", new WebSocketStompFrameEncoder() );
        pipeline.addLast( "stomp-frame-decoder", new WebSocketStompFrameDecoder() );

        appendCommonHandlers( pipeline );

        return fullBuffer;
    }

    protected void appendCommonHandlers(ChannelPipeline pipeline) {

        ConnectionContext context = new ConnectionContext();


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
        //pipeline.addLast( "debug-TAIL", new DebugHandler( "stomp.proto.SERVER-TAIL" ) );
    }

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    private static final Logger log = Logger.getLogger( "org.projectodd.stilts.stomp.server.protocol" );

    private StompProvider provider;
    private Executor executor;
    private ExecutionHandler executionHandler;

}
