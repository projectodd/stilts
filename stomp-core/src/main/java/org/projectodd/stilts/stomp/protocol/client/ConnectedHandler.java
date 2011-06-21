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
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;

public class ConnectedHandler extends AbstractClientControlFrameHandler {

    public ConnectedHandler(ClientContext clientContext) {
        super( clientContext, Command.CONNECTED );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info(  "Received: " + frame  );
        getClientContext().setConnectionState( State.CONNECTED );
    }

}
