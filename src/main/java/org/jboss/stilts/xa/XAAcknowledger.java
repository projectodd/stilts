package org.jboss.stilts.xa;

import org.jboss.stilts.spi.Acknowledger;

public class XAAcknowledger implements Acknowledger {

    public XAAcknowledger(Acknowledger acknowledger) {
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws Exception {
        XATransaction tx = StompProviderResourceManager.currentTransaction();
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
        XATransaction tx = StompProviderResourceManager.currentTransaction();
        if (tx != null) {
            System.err.println( "Transactional nack" );
            tx.addNack(  this.acknowledger );
        } else {
            System.err.println( "Direct nack: " + this.acknowledger );
            this.acknowledger.nack();
        }
    }
    
    private Acknowledger acknowledger;


}
