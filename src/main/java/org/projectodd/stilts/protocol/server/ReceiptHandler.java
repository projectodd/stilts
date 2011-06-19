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
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.StompProvider;

public class ReceiptHandler extends AbstractProviderHandler {

    public ReceiptHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompFrame) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        }
        super.messageReceived( channelContext, e );
    }

    public void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info(  "Checking receipt for: " + frame  );
        if ( ! getContext().isActive() ) {
            log.info( "Connection not active, no ACK required" );
            return;
        }
        String receiptId = frame.getHeader( Header.RECEIPT );
        if ( receiptId != null ) {
            StompFrame receipt = StompFrames.newReceiptFrame(receiptId);
            sendFrame( channelContext, receipt );
        }
    }
    
}
