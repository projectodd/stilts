package org.jboss.stilts.client;

import org.jboss.stilts.StompException;

public interface ClientSubscription {
    
    String getId();
    void unsubscribe() throws StompException;
    ClientTransaction getTransaction();
    MessageHandler getMessageHandler();
    
    boolean isActive();

}
