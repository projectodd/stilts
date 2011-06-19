package org.projectodd.stilts.client;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.client.StompClient.State;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.protocol.client.ClientContext;

public class DefaultClientContext implements ClientContext {
    
    public DefaultClientContext(AbstractStompClient client) {
        this.client = client;
    }

    @Override
    public LoggerManager getLoggerManager() {
        return this.client.getLoggerManager();
    }

    @Override
    public State getConnectionState() {
        return this.client.getConnectionState();
    }

    @Override
    public void setConnectionState(State connectionState) {
        this.client.setConnectionState( connectionState );
    }

    private AbstractStompClient client;

    @Override
    public void messageReceived(StompMessage message) {
        this.client.messageReceived( message );
    }

    @Override
    public void errorReceived(StompMessage message) {
        this.client.errorReceived( message );
    }

    @Override
    public void receiptReceived(String receiptId) {
        this.client.receiptReceived( receiptId );
    }

}
