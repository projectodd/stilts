package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Acknowledger;

public class StompletAcknowledger implements Acknowledger {


    public StompletAcknowledger(AcknowledgeableStomplet stomplet, Subscriber subscriber, StompMessage message) {
        this.stomplet = stomplet;
        this.subscriber = subscriber;
        this.message = message;
        System.err.println( "CREATE ACK FOR : " + message );
    }
    
    @Override
    public void ack() throws Exception {
        this.stomplet.ack( this.subscriber, this.message );
    }

    @Override
    public void nack() throws Exception {
        this.stomplet.nack( this.subscriber, this.message );
    }
    
    private AcknowledgeableStomplet stomplet;
    private Subscriber subscriber;
    private StompMessage message;

}
