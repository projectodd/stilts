package org.projectodd.stilts.stomp.server.protocol;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.stomp.TransactionalAcknowledger;

public class MockTransactionalAcknowledger implements TransactionalAcknowledger {

    @Override
    public void ack(String transactionId) throws Exception {
        this.acks.add( transactionId );
    }
    
    public List<String> getAcks() {
        return this.acks;
    }

    @Override
    public void nack(String transactionId) throws Exception {
        this.nacks.add( transactionId );
    }
    
    public List<String> getNacks() {
        return this.nacks;
    }
    
    public List<String> acks = new ArrayList<String>();
    public List<String> nacks = new ArrayList<String>();

}
