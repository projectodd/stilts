package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.spi.Subscription;

public class StompletSubscription implements Subscription {

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

    private Stomplet stomplet;
    private Subscriber subscriber;

}
