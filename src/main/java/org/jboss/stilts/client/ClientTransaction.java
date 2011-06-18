package org.jboss.stilts.client;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface ClientTransaction {
    
    String getId();
    
    void send(StompMessage message) throws StompException;
    
    void commit() throws StompException;
    void abort() throws StompException;

}
