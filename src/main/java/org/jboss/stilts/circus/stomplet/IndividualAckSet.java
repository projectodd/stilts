package org.jboss.stilts.circus.stomplet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.stilts.spi.Acknowledger;

public class IndividualAckSet implements AckSet {

    public IndividualAckSet() {
        
    }

    @Override
    public void ack(String messageId) throws Exception {
        Acknowledger ack = this.acknowledgers.remove( messageId );
        if ( ack != null ) {
            ack.ack();
        }
    }

    @Override
    public void nak(String messageId) throws Exception {
        Acknowledger ack = this.acknowledgers.remove( messageId );
        if ( ack != null ) {
            ack.nack();
        }
    }

    @Override
    public void addAcknowledger(String messageId, Acknowledger acknowledger) {
        this.acknowledgers.put( messageId, acknowledger );
    }
    
    private Map<String, Acknowledger> acknowledgers = new ConcurrentHashMap<String, Acknowledger>();
}
