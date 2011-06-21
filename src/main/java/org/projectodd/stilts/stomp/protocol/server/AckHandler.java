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

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.StompProvider;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class AckHandler extends AbstractControlFrameHandler {

    public AckHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.ACK );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String messageId = frame.getHeader( Header.MESSAGE_ID );
        Acknowledger acknowledger = getContext().getAckManager().removeAcknowledger( messageId );
        String transactionId = frame.getHeader( Header.TRANSACTION );
        System.err.println( "--------" );
        System.err.println( "A: " + acknowledger );
        System.err.println( "M: " + messageId );
        System.err.println( "T: " + transactionId );
        System.err.println( "--------" );
        if ( acknowledger != null ) {
            try {
                getStompConnection().ack( acknowledger, transactionId );
            } catch (Exception e) {
                sendError( channelContext, "Unable to ACK", frame );
            }
        }
    }

}
