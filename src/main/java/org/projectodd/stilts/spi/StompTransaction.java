package org.projectodd.stilts.spi;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface StompTransaction {
    void commit() throws StompException;
    void abort() throws StompException;
    
    void send(StompMessage message) throws StompException;
}
