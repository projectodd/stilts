package org.jboss.stilts.client;

import org.jboss.stilts.StompException;

public interface ClientSubscription {
    
    String getId();
    void unsubscribe() throws StompException;
    MessageHandler getMessageHandler();
    
    boolean isActive();

}
