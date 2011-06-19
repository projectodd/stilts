package org.jboss.stilts.circus.stomplet;

import org.jboss.stilts.spi.Acknowledger;

public interface AckSet {
    
    void ack(String messageId) throws Exception;
    void nak(String messageId) throws Exception;
    void addAcknowledger(String messageId, Acknowledger acknowledger);

}
