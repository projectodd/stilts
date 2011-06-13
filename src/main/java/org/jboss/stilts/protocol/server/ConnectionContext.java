package org.jboss.stilts.protocol.server;

import org.jboss.stilts.spi.ClientAgent;

public class ConnectionContext {
    
    public ConnectionContext() {
        
    }
    
    public void setClientAgent(ClientAgent clientAgent) {
        this.clientAgent = clientAgent;
    }
    
    public ClientAgent getClientAgent() {
        return this.clientAgent;
    }
    
    private ClientAgent clientAgent;

}
