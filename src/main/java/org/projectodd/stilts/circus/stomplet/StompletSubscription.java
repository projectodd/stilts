package org.projectodd.stilts.circus.stomplet;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.Subscription;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;

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
