package org.jboss.stilts.protocol.server;

import org.jboss.stilts.logging.LoggerManager;
import org.jboss.stilts.spi.ClientAgent;

public class ConnectionContext {
    
    public ConnectionContext(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }
    
    public LoggerManager getLoggerManager() {
        return this.loggerManager;
    }
    
    public void setClientAgent(ClientAgent clientAgent) {
        this.clientAgent = clientAgent;
    }
    
    public ClientAgent getClientAgent() {
        return this.clientAgent;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    private LoggerManager loggerManager;
    private ClientAgent clientAgent;
    private boolean active = true;

}
