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
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class SendHandler extends AbstractProviderHandler {

    public SendHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log.info( "SEND: " + e.getMessage() );
        if (e.getMessage() instanceof StompMessage) {
            log.info( "SEND: " + e.getMessage() + " via " + getContext()  );
            log.info( "SEND: " + e.getMessage() + " via " + getContext().getStompConnection()  );
            StompMessage message = (StompMessage) e.getMessage();
            String transactionId = message.getHeaders().get( Header.TRANSACTION );
            getContext().getStompConnection().send( (StompMessage) e.getMessage(), transactionId );
        }
        super.messageReceived( ctx, e );
    }

}
