package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.StompMessage;

public interface AcknowledgeableStomplet extends Stomplet {
    
    void ack(Subscriber subscriber, StompMessage message);
    void nack(Subscriber subscriber, StompMessage message);

}
