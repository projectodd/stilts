package org.jboss.stilts.circus.xa.psuedo;

import javax.transaction.xa.XAResource;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.circus.xa.XAMessageConduit;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

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
