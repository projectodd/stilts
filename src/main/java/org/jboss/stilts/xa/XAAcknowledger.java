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
            tx.addAck( this.acknowledger );
        } else {
            this.acknowledger.ack();
        }
    }

    @Override
    public void nack() throws Exception {
        XATransaction tx = StompProviderResourceManager.currentTransaction();
        if (tx != null) {
            tx.addAck( this.acknowledger );
        } else {
            this.acknowledger.ack();
        }
    }
    
    private Acknowledger acknowledger;


}
