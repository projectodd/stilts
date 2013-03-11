package org.projectodd.stilts.stomp.protocol.longpoll;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrameCodec;

public class HttpServerStompFrameEncoder extends OneToOneEncoder {

    public HttpServerStompFrameEncoder() {
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompFrame) {
            ChannelBuffer buffer = StompFrameCodec.INSTANCE.encode( (StompFrame) msg );
            HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
            httpResp.setContent( buffer );
            httpResp.setHeader( "Content-Length", "" + buffer.readableBytes() );
            httpResp.setHeader( "Content-Type", "text/stomp" );
            return httpResp;
        }
        return msg;
    }

}
