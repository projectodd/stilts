/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.circus.xa.psuedo;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.spi.Acknowledger;

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
