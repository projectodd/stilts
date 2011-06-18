package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.Subscription;

public class StompletSubscription implements Subscription, AcknowledgeableMessageSink {

    public StompletSubscription(Stomplet stomplet, Subscriber subscriber) {
        this.stomplet = stomplet;
        this.subscriber = subscriber;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void cancel() throws StompException {
        stomplet.onUnsubscribe( this.subscriber );
    }

    @Override
    public void send(StompMessage message) throws StompException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        // TODO Auto-generated method stub
        
    }
    
    private Stomplet stomplet;
    private Subscriber subscriber;

}
