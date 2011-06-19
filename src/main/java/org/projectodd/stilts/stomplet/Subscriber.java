package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Subscription.AckMode;

public interface Subscriber extends AcknowledgeableMessageSink {
    
    String getId();
    String getDestination();
    AckMode getAckMode();
    //void send(StompMessage message) throws StompException;

}
