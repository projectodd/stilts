package org.projectodd.stilts.stomp.server.protocol;

import org.projectodd.stilts.stomp.spi.StompConnection;

public class WrappedConnectionContext implements ConnectionContext {
    
    public WrappedConnectionContext() {
    }
    
    public void setConnectionContext(ConnectionContext context) {
        this.context = context;
    }
    
    public ConnectionContext getConnectionContext() {
        return this.context;
    }

    @Override
    public AckManager getAckManager() {
        return this.context.getAckManager();
    }

    @Override
    public void setStompConnection(StompConnection clientAgent) {
        this.context.setStompConnection( clientAgent );
    }

    @Override
    public StompConnection getStompConnection() {
        return this.context.getStompConnection();
    }

    @Override
    public boolean isActive() {
        return this.context.isActive();
    }

    @Override
    public void setActive(boolean active) {
        this.context.setActive( active );
    }

    private ConnectionContext context;

}
