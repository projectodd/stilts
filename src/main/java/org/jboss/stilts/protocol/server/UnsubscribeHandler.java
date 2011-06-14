package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompProvider;

public class UnsubscribeHandler extends AbstractControlFrameHandler {

    public UnsubscribeHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.UNSUBSCRIBE );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String destinationOrId = frame.getHeader( Header.DESTINATION );
        if ( destinationOrId == null ) {
            destinationOrId = frame.getHeader( Header.ID );
        }
        
        if ( destinationOrId == null ) {
            sendError( channelContext, "Must supply 'destination' or 'id' header for UNSUBSCRIBE", frame );
            return;
        }
        
        try {
            getClientAgent().unsubscribe( destinationOrId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, e.getMessage(), frame );
        }
    }
    
}
