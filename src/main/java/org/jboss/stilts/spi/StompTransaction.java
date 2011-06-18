package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface StompTransaction {
    void commit() throws StompException;
    void abort() throws StompException;
    
    void send(StompMessage message) throws StompException;
}
