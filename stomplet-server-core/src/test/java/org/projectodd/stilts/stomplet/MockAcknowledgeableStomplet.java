package org.projectodd.stilts.stomplet;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.stomp.StompMessage;

public class MockAcknowledgeableStomplet extends MockStomplet implements AcknowledgeableStomplet {

    @Override
    public void ack(Subscriber subscriber, StompMessage message) {
        this.acks.add( new Ack( subscriber, message ) );
    }
    
    public List<Ack> getAcks() {
        return this.acks;
    }

    @Override
    public void nack(Subscriber subscriber, StompMessage message) {
        this.nacks.add( new Ack( subscriber, message ) );
    }
    
    public List<Ack> getNacks() {
        return this.nacks;
    }
    
    private List<Ack> acks = new ArrayList<Ack>();
    private List<Ack> nacks = new ArrayList<Ack>();
    
    public static class Ack {
        public Ack(Subscriber subscriber, StompMessage message) {
            this.subscriber = subscriber;
            this.message = message;
        }
        
        public Subscriber subscriber;
        public StompMessage message;
    }

}
