package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.StompProvider;

public class BeginHandler extends AbstractControlFrameHandler {

    public BeginHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.BEGIN );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String transactionId = frame.getHeader( Header.TRANSACTION );
        try {
            getStompConnection().begin( transactionId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, "Unable to begin transaction: " + e.getMessage(), frame );
        }
    }

}
