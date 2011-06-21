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

package org.projectodd.stilts.stomp.client;

import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.spi.Acknowledger;
import org.projectodd.stilts.stomp.spi.Headers;

public class ClientMessageAcknowledger implements Acknowledger {

    public ClientMessageAcknowledger(AbstractStompClient client, Headers headers) {
        this.client = client;
        this.headers = headers;
    }

    @Override
    public void ack() throws Exception {
        client.sendFrame( StompFrames.newAckFrame( this.headers ) );
    }

    @Override
    public void nack() throws Exception {
        client.sendFrame( StompFrames.newNackFrame( this.headers ) );
    }

    private AbstractStompClient client;
    private Headers headers;


}
