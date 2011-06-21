/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

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
