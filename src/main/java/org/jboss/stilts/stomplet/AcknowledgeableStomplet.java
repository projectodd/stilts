package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompMessage;

public interface AcknowledgeableStomplet extends Stomplet {
    
    void ack(Subscriber subscriber, StompMessage message);
    void nak(Subscriber subscriber, StompMessage message);

}
