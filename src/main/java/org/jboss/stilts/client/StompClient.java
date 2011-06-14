package org.jboss.stilts.client;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

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
    
}
