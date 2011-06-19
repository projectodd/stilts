package org.projectodd.stilts.client;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface StompClient {
    
    public static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }
    
    boolean isConnected();
    
    void connect() throws InterruptedException, StompException;
    void disconnect() throws InterruptedException, StompException;
    
    SubscriptionBuilder subscribe(String destination);
    void send(StompMessage message) throws StompException;
    
    ClientTransaction begin() throws StompException;
    void commit(String transactionId) throws StompException;
    void abort(String transactionId) throws StompException;
    
}
