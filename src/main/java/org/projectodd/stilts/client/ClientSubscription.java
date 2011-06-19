package org.projectodd.stilts.client;

import org.projectodd.stilts.StompException;

public interface ClientSubscription {
    
    String getId();
    void unsubscribe() throws StompException;
    MessageHandler getMessageHandler();
    
    boolean isActive();

}
