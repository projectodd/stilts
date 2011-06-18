package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompProvider;

public class CommitHandler extends AbstractControlFrameHandler {

    public CommitHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.COMMIT );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getClientAgent().commit( transactionId );
        } catch (StompException e) {
            sendError( channelContext, "Unable to commit transaction: " + e.getMessage(), frame );
        }
    }

}
