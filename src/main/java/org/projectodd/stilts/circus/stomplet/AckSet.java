package org.projectodd.stilts.circus.stomplet;

import org.projectodd.stilts.spi.Acknowledger;

public interface AckSet {
    
    void ack(String messageId) throws Exception;
    void nak(String messageId) throws Exception;
    void addAcknowledger(String messageId, Acknowledger acknowledger);

}
