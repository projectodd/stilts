package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.StompProvider;

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
            getStompConnection().unsubscribe( destinationOrId, frame.getHeaders() );
        } catch (StompException e) {
            sendError( channelContext, e.getMessage(), frame );
        }
    }
    
}
