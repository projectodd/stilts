/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

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
