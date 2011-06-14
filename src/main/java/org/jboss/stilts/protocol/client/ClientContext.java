package org.jboss.stilts.protocol.client;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.client.StompClient.State;
import org.jboss.stilts.logging.LoggerManager;

public interface ClientContext {
    
    LoggerManager getLoggerManager();
    State getConnectionState();
    void setConnectionState(State state);
    
    void receiptReceived(String receiptId);
    void messageReceived(StompMessage message);
    void errorReceived(StompMessage message);
}
