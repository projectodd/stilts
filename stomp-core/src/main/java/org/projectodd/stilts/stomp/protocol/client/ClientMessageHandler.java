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

package org.projectodd.stilts.stomp.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.StompMessage;

public class ClientMessageHandler extends AbstractClientHandler {

    public ClientMessageHandler(ClientContext clientContext) {
        super( clientContext );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        log.info( "reveived: " + e.getMessage() );
        if ( e.getMessage() instanceof StompMessage ) {
            handleStompMessage( channelContext, (StompMessage) e.getMessage() );
        } 
        super.messageReceived( channelContext, e );
    }

    protected void handleStompMessage(ChannelHandlerContext channelContext, StompMessage message) {
        if ( message.isError() ) {
            getClientContext().errorReceived( message );
        } else {
            getClientContext().messageReceived( message );
        }
    }


}
