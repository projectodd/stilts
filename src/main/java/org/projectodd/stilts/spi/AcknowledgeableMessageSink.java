package org.projectodd.stilts.spi;

import org.projectodd.stilts.MessageSink;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface AcknowledgeableMessageSink extends MessageSink {
    
    void send(StompMessage message, Acknowledger acknowledger) throws StompException;

}
