package org.projectodd.stilts.stomp.server.protocol.http;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrameCodec;

public class HttpServerStompFrameEncoder extends OneToOneEncoder implements ChannelUpstreamHandler {

    public HttpServerStompFrameEncoder() {
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        log.debugf( "encode %s", msg );
        if (msg instanceof StompFrame) {
            if (this.stream) {
                log.debug( "encore for STREAM" );
                ChannelBuffer buffer = StompFrameCodec.INSTANCE.encode( (StompFrame) msg );
                ChannelBuffer sseBuffer = ChannelBuffers.dynamicBuffer();
                sseBuffer.writeByte( (byte) '\n' );
                while (true) {
                    int newLineLoc = buffer.bytesBefore( (byte) '\n' );
                    if (newLineLoc < 0) {
                        break;
                    }
                    sseBuffer.writeBytes( "data:".getBytes() );
                    sseBuffer.writeBytes( buffer.readBytes( newLineLoc + 1 ) );
                }
                if (buffer.readableBytes() > 0) {
                    sseBuffer.writeBytes( "data:".getBytes() );
                    sseBuffer.writeBytes( buffer.readBytes( buffer.readableBytes() ) );
                    sseBuffer.writeByte( (byte) '\n' );
                }
                sseBuffer.writeByte( (byte) '\n' );
                return sseBuffer;
            } else {
                log.debug( "encore for NOT_STREAM" );
                ChannelBuffer buffer = StompFrameCodec.INSTANCE.encode( (StompFrame) msg );
                HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
                httpResp.setContent( buffer );
                httpResp.setHeader( "Content-Length", "" + buffer.readableBytes() );
                httpResp.setHeader( "Content-Type", "text/stomp" );
                return httpResp;
            }
        }
        return msg;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            if (((MessageEvent) e).getMessage() instanceof HttpRequest) {
                HttpRequest httpReq = (HttpRequest) ((MessageEvent) e).getMessage();
                String contentType = httpReq.getHeader( "Accept" );
                if (contentType != null) {
                    if (contentType.equals( "text/event-stream" )) {
                        this.stream = true;
                    } else {
                        this.stream = false;
                    }
                }
            }
        }
        ctx.sendUpstream( e );
    }

    private static final Logger log = Logger.getLogger( HttpServerStompFrameEncoder.class );

    private boolean stream;

}
