package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompServer;

public class AbortHandler extends AbstractControlFrameHandler {

    public AbortHandler(StompServer server, ConnectionContext context) {
        super( server, context, Command.ABORT );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getClientAgent().abort( transactionId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, "Unable to abort transaction: " + e.getMessage() );
        }
    }

}
