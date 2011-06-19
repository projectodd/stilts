package org.projectodd.stilts.protocol.client;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.client.StompClient.State;
import org.projectodd.stilts.logging.LoggerManager;

public interface ClientContext {
    
    LoggerManager getLoggerManager();
    State getConnectionState();
    void setConnectionState(State state);
    
    void receiptReceived(String receiptId);
    void messageReceived(StompMessage message);
    void errorReceived(StompMessage message);
}
