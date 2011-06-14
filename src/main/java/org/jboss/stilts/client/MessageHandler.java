package org.jboss.stilts.client;

import org.jboss.stilts.StompMessage;

public interface MessageHandler {
    
    void handle(StompMessage message);

}
