package org.jboss.stilts.xa;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.StompProvider;

class XATransaction {
    
    private boolean rollbackOnly;

    XATransaction() {
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
    
    public void commit(StompProvider provider) {
        for (StompMessage each : sentMessages ) {
            try {
                provider.send( each );
            } catch (StompException e) {
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
    
    public void rollback(StompProvider provider) {
        sentMessages.clear();
        acks.clear();
    }

    private ConcurrentLinkedQueue<StompMessage> sentMessages = new ConcurrentLinkedQueue<StompMessage>();
    private ConcurrentLinkedQueue<Acknowledger> acks = new ConcurrentLinkedQueue<Acknowledger>();
    private ConcurrentLinkedQueue<Acknowledger> nacks = new ConcurrentLinkedQueue<Acknowledger>();

}
