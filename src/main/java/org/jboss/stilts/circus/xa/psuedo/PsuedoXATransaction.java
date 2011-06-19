package org.jboss.stilts.circus.xa.psuedo;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.circus.MessageConduit;
import org.jboss.stilts.spi.Acknowledger;

class PsuedoXATransaction {
    
    PsuedoXATransaction() {
    }
    
    void setRollbackOnly(boolean rollbackOnly) {
        this.rollbackOnly = rollbackOnly;
    }
    
    boolean isRollbackOnly() {
        return this.rollbackOnly;
    }
    
    boolean isReadOnly() {
        return (this.sentMessages.isEmpty() && this.acks.isEmpty() && this.nacks.isEmpty() );
    }
    
    void addSentMessage(StompMessage message) {
        this.sentMessages.add( message );
    }
    
    boolean hasSentMessages() {
        return ! this.sentMessages.isEmpty();
    }
    
    void addAck(Acknowledger acknowledger) {
        this.acks.add(  acknowledger );
    }
    
    boolean hasAcks() {
        return ! this.acks.isEmpty();
    }
    
    void addNack(Acknowledger acknowledger) {
        this.nacks.add(acknowledger);
    }
    
    boolean hasNacks() {
        return ! this.nacks.isEmpty();
    }
    
    public void commit(MessageConduit conduit) {
        for (StompMessage each : sentMessages ) {
            try {
                conduit.send( each );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        sentMessages.clear();
        
        for ( Acknowledger each : this.acks ) {
            try {
                each.ack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        this.acks.clear();
        
        for ( Acknowledger each : this.nacks ) {
            try {
                each.nack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        this.nacks.clear();
    }
    
    public void rollback(MessageConduit messageConduit) {
        sentMessages.clear();
        acks.clear();
    }

    private boolean rollbackOnly;

    private ConcurrentLinkedQueue<StompMessage> sentMessages = new ConcurrentLinkedQueue<StompMessage>();
    private ConcurrentLinkedQueue<Acknowledger> acks = new ConcurrentLinkedQueue<Acknowledger>();
    private ConcurrentLinkedQueue<Acknowledger> nacks = new ConcurrentLinkedQueue<Acknowledger>();

}
