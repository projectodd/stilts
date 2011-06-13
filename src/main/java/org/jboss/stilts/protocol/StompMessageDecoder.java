package org.jboss.stilts.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class StompMessageDecoder extends OneToOneDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if ( msg instanceof StompContentFrame ) {
           StompContentFrame frame = (StompContentFrame) msg; 
           return new DefaultStompServerMessage( frame.getHeaders(), frame.getContent() );
        }
        return null;
    }

}
