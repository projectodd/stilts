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

package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.Acknowledger;
import org.projectodd.stilts.stomp.spi.Subscription;
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
