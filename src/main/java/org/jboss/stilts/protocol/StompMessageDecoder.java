package org.jboss.stilts.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.jboss.stilts.logging.Logger;
import org.jboss.stilts.protocol.StompFrame.Command;

public class StompMessageDecoder extends OneToOneDecoder {
    
    public StompMessageDecoder(Logger log) {
        this.log = log;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        log.info( "message decode: " + msg );
        if (msg instanceof StompContentFrame) {
            StompContentFrame frame = (StompContentFrame) msg;
            boolean isError = false;
            if (frame.getCommand() == Command.ERROR) {
                isError = true;
            }
            return new DefaultStompServerMessage( frame.getHeaders(), frame.getContent(), isError );
        }
        return null;
    }

    private Logger log;

}
