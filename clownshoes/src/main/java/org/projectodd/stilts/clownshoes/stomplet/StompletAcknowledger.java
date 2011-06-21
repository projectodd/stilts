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

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.spi.Acknowledger;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class StompletAcknowledger implements Acknowledger {


    public StompletAcknowledger(AcknowledgeableStomplet stomplet, Subscriber subscriber, StompMessage message) {
        this.stomplet = stomplet;
        this.subscriber = subscriber;
        this.message = message;
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
