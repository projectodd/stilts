package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface MessageRouter {
    
    void send(StompMessage message) throws StompException;

}
