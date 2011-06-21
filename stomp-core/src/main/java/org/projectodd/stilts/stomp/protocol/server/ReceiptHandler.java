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
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

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
