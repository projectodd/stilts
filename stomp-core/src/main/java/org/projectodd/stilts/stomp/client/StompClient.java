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

public interface StompClient {
    
    public static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }
    
    boolean isConnected();
    
    void connect() throws InterruptedException, StompException;
    void disconnect() throws InterruptedException, StompException;
    
    SubscriptionBuilder subscribe(String destination);
    void send(StompMessage message) throws StompException;
    
    ClientTransaction begin() throws StompException;
    void commit(String transactionId) throws StompException;
    void abort(String transactionId) throws StompException;
    
}
