package org.jboss.stilts.spi;

import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface ClientAgent {
    
    String getSessionId();

    void send(StompMessage message, String transactionId) throws StompException;
    void onMessage(StompMessage message, Acknowledger acknowledger) throws StompException;
    
    Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException;
    void unsubscribe(String subscriptionId, Headers headers) throws StompException;
    
    void begin(String transactionId, Headers headers) throws StompException;
    void commit(String transactionId) throws StompException;
    void abort(String transactionId) throws StompException;
    
    void ack(Acknowledger acknowledger, String transactionId) throws StompException;
    void nack(Acknowledger acknowledger, String transactionId) throws StompException;
    
    void disconnect() throws NotConnectedException;

    
    
}
