package org.jboss.stilts.spi;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface AcknowledgeableMessageSink extends MessageSink {
    
    void send(StompMessage message, Acknowledger acknowledger) throws StompException;

}
