package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompServer;

public class BeginHandler extends AbstractControlFrameHandler {

    public BeginHandler(StompServer server, ConnectionContext context) {
        super( server, context, Command.BEGIN );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getClientAgent().begin( transactionId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, "Unable to begin transaction: " + e.getMessage() );
        }
    }

}
