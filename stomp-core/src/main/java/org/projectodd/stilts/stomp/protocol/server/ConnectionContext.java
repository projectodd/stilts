/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
