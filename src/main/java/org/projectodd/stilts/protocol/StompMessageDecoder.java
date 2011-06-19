package org.projectodd.stilts.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.spi.StompMessageFactory;

public class StompMessageDecoder extends OneToOneDecoder {
    
    private StompMessageFactory messageFactory;

    public StompMessageDecoder(Logger log, StompMessageFactory messageFactory) {
        this.log = log;
        this.messageFactory = messageFactory;
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
            return this.messageFactory.createMessage( frame.getHeaders(), frame.getContent(), isError );
        }
        return null;
    }

    private Logger log;

}
