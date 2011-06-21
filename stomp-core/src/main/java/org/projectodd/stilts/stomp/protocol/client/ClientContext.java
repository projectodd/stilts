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

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.stomp.client.StompClient.State;

public interface ClientContext {
    
    LoggerManager getLoggerManager();
    State getConnectionState();
    void setConnectionState(State state);
    
    void receiptReceived(String receiptId);
    void messageReceived(StompMessage message);
    void errorReceived(StompMessage message);
}
