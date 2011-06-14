package org.jboss.stilts.client;

import org.jboss.stilts.StompMessage;

public interface ClientTransaction {
    
    String getId();
    
    SubscriptionBuilder subscribe(String destination);
    
    void send(StompMessage message);
    
    void commit();
    void abort();

}
