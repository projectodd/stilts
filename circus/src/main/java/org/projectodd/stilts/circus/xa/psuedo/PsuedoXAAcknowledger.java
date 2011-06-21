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

package org.projectodd.stilts.circus.xa.psuedo;

import org.projectodd.stilts.stomp.spi.Acknowledger;

public class PsuedoXAAcknowledger implements Acknowledger {

    public PsuedoXAAcknowledger(PsuedoXAResourceManager resourceManager, Acknowledger acknowledger) {
        this.resourceManager = resourceManager;
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addAck( this.acknowledger );
        } else {
            this.acknowledger.ack();
        }
    }

    @Override
    public void nack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addNack(  this.acknowledger );
        } else {
            this.acknowledger.nack();
        }
    }
    
    private PsuedoXAResourceManager resourceManager;
    private Acknowledger acknowledger;


}
