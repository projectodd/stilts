package org.projectodd.stilts.stomplet.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomplet.Subscriber;

public class SubscriberList {

    public SubscriberList() {
    }

    public synchronized int size() {
        return this.subscribers.size();
    }

    public synchronized void addSubscriber(Subscriber subscriber) {
        this.subscribers.add( subscriber );
    }

    public synchronized boolean removeSubscriber(Subscriber subscriber) {
        return this.subscribers.remove( subscriber );
    }

    protected synchronized void sendToAllSubscribers(StompMessage message) throws StompException {
        for (Subscriber each : this.subscribers) {
            System.err.println( "SUBSCRIBER: " + each + " // " + message );
            each.send( message );
        }
    }

    protected synchronized void sendToOneSubscriber(StompMessage message) throws StompException {
        int luckyWinner = this.random.nextInt( this.subscribers.size() );
        this.subscribers.get( luckyWinner ).send( message );
    }

    private final List<Subscriber> subscribers = new ArrayList<Subscriber>();
    private Random random = new Random( System.currentTimeMillis() );

}
