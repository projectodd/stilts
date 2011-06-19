package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompProvider;

public class AbortHandler extends AbstractControlFrameHandler {

    public AbortHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.ABORT );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getStompConnection().abort( transactionId );
        } catch (StompException e) {
            sendError( channelContext, "Unable to abort transaction: " + e.getMessage(), frame );
        }
    }

}
