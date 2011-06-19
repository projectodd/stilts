package org.projectodd.stilts.client;

import org.projectodd.stilts.StompMessage;

public interface MessageHandler {
    
    void handle(StompMessage message);

}
