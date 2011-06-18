package org.jboss.stilts.stomplet;

import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Subscription.AckMode;

public interface Subscriber extends AcknowledgeableMessageSink {
    
    String getId();
    String getDestination();
    AckMode getAckMode();
    //void send(StompMessage message) throws StompException;

}
