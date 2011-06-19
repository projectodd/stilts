package org.jboss.stilts.circus.xa.psuedo;

import org.jboss.stilts.spi.Acknowledger;

public class PsuedoXAAcknowledger implements Acknowledger {

    public PsuedoXAAcknowledger(PsuedoXAResourceManager resourceManager, Acknowledger acknowledger) {
        this.resourceManager = resourceManager;
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            System.err.println( "Transactional ack" );
            tx.addAck( this.acknowledger );
        } else {
            System.err.println( "Direct ack" );
            this.acknowledger.ack();
        }
    }

    @Override
    public void nack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            System.err.println( "Transactional nack" );
            tx.addNack(  this.acknowledger );
        } else {
            System.err.println( "Direct nack: " + this.acknowledger );
            this.acknowledger.nack();
        }
    }
    
    private PsuedoXAResourceManager resourceManager;
    private Acknowledger acknowledger;


}
