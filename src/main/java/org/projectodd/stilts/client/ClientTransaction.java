package org.projectodd.stilts.client;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface ClientTransaction {
    
    String getId();
    
    void send(StompMessage message) throws StompException;
    
    void commit() throws StompException;
    void abort() throws StompException;

}
