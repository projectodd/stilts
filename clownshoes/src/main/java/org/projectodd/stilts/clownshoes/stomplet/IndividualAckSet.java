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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.projectodd.stilts.stomp.spi.Acknowledger;

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
