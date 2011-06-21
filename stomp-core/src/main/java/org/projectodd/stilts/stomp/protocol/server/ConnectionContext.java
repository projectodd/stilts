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

import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class ConnectionContext {
    
    public ConnectionContext(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
        this.ackManager = new AckManager();
    }
    
    AckManager getAckManager() {
        return this.ackManager;
    }
    
    public LoggerManager getLoggerManager() {
        return this.loggerManager;
    }
    
    public void setStompConnection(StompConnection clientAgent) {
        this.stompConnection = clientAgent;
    }
    
    public StompConnection getStompConnection() {
        return this.stompConnection;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    private AckManager ackManager;
    private LoggerManager loggerManager;
    private StompConnection stompConnection;
    private boolean active = true;

}
