package org.jboss.stilts.spi;

import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface ClientAgent {
    
    String getSessionId();

    void send(StompMessage message) throws StompException;
    void onMessage(StompMessage message) throws StompException;
    
    Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException;
    void unsubscribe(String subscriptionId, Headers headers) throws StompException;
    
    void begin(String transactionId, Headers headers) throws StompException;
    void commit(String transactionId, Headers headers) throws StompException;
    void abort(String transactionId, Headers headers) throws StompException;
    void ack(String messageId, String transactionId, Headers headers) throws StompException, StompException;
    
    void disconnect() throws NotConnectedException;
    
    
}
