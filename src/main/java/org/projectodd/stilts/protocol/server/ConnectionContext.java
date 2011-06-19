package org.projectodd.stilts.protocol.server;

import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.spi.StompConnection;

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
