package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface StompProvider {
    
    ClientAgent connect(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException;
    void send(StompMessage message) throws StompException;
}
