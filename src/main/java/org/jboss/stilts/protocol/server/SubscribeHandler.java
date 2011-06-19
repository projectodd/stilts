package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.StompProvider;

public class SubscribeHandler extends AbstractControlFrameHandler {

    public SubscribeHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.SUBSCRIBE );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info( "Subscribing for frame: " + frame );
        String destination = frame.getHeader( Header.DESTINATION );
        String id = frame.getHeader( Header.ID );
        try {
            getStompConnection().subscribe( destination, id, frame.getHeaders() );
        } catch (StompException e) {
            log.error( "Error performing subscription to '" + destination + "' for id '" + id + "'", e );
            sendError( channelContext, e.getMessage(), frame );
        }
    }

}
