package org.projectodd.stilts.conduit.stomp;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.TransactionalAcknowledger;

public class ConduitAcknowledger implements TransactionalAcknowledger {
    
    public ConduitAcknowledger(ConduitStompConnection connection, Acknowledger acknowledger) {
        this.connection = connection;
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack(String transactionId) throws Exception {
        this.connection.ack( acknowledger, transactionId );

    }

    @Override
    public void nack(String transactionId) throws Exception {
        this.connection.nack( acknowledger, transactionId );

    }
    
    private ConduitStompConnection connection;
    private Acknowledger acknowledger;

}
