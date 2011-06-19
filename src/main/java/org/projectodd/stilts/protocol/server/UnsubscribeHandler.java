/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
