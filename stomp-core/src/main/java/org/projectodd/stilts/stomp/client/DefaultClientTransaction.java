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

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class DefaultClientTransaction implements ClientTransaction {

    public DefaultClientTransaction(AbstractStompClient client, String id) {
        this( client, id, false );
    }

    public DefaultClientTransaction(AbstractStompClient client, String id, boolean isGlobal) {
        this.client = client;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void send(StompMessage message) {
        message.getHeaders().put( Header.TRANSACTION, this.id );
        this.client.send( message );
    }

    @Override
    public void commit() throws StompException {
        this.client.commit( this.id );
    }

    @Override
    public void abort() throws StompException {
        this.client.abort( this.id );
    }

    private AbstractStompClient client;
    private String id;
}
