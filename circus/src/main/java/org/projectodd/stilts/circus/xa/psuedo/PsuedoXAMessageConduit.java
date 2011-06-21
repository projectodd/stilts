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

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.Subscription;

public class PsuedoXAMessageConduit implements XAMessageConduit {

    public PsuedoXAMessageConduit(PsuedoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void send(StompMessage stompMessage) throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if ( tx == null ) {
            this.resourceManager.getMessageConduit().send( stompMessage );
        } else {
            tx.addSentMessage( stompMessage );
        }
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        return this.resourceManager.getMessageConduit().subscribe( subscriptionId, destination, headers );
    }

    @Override
    public XAResource getXAResource() {
        return this.resourceManager;
    }


    private PsuedoXAResourceManager resourceManager;

}
