package org.projectodd.stilts.stomp.protocol.longpoll;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrameCodec;

public class HttpClientStompFrameEncoder extends OneToOneEncoder {

    public HttpClientStompFrameEncoder(String uri) {
        this.uri = uri;
    }
    
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if ( msg instanceof StompFrame ) {
            ChannelBuffer buffer = StompFrameCodec.INSTANCE.encode( (StompFrame) msg );
            HttpRequest httpReq = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.POST, this.uri );
            httpReq.setContent(  buffer );
            return httpReq;
        }
        return msg;
    }
    
    private String uri;

}
