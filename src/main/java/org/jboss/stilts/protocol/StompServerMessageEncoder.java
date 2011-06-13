package org.jboss.stilts.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Command;

public class StompServerMessageEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompMessage) {
            StompMessage message = (StompMessage) msg;
            FrameHeader header = new FrameHeader( Command.MESSAGE, message.getHeaders() );
            StompContentFrame frame = new StompContentFrame( header, message.getContent() );
            return frame;
        }
        return null;
    }

}
