package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompServer;

public class SubscribeHandler extends AbstractControlFrameHandler {

    public SubscribeHandler(StompServer server, ConnectionContext context) {
        super( server, context, Command.SUBSCRIBE );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String destination = frame.getHeader( Header.DESTINATION );
        String id = frame.getHeader( Header.ID );
        try {
            getClientAgent().subscribe( destination, id, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, e.getMessage() );
        }
    }
    
}
