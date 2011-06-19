package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface MessageRouter {
    
    void send(StompMessage message) throws StompException;

}
