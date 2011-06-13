package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface Transaction {
    void commit() throws StompException;
    void abort() throws StompException;
    
    Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException;
    void unsubscribe(String subscriptionId) throws StompException;
    void ack(String messageId) throws StompException;
    
    void close() throws StompException;
    void send(StompMessage message) throws StompException;
}
