package org.projectodd.stilts.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame.Command;

public class StompMessageEncoder extends OneToOneEncoder {
    
    public StompMessageEncoder(Logger log) {
        this.log = log; 
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompFrame ) {
            return msg;
        }
        if (msg instanceof StompMessage) {
            StompMessage message = (StompMessage) msg;
            log.trace(  "encode: " + message );
            FrameHeader header = new FrameHeader( Command.MESSAGE, message.getHeaders() );
            StompContentFrame frame = new StompContentFrame( header, message.getContent() );
            log.trace(  "encode.frame: " + frame );
            return frame;
        }
        return null;
    }

    private Logger log;

}
