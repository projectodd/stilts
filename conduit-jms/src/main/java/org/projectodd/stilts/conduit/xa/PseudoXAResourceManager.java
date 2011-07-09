/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.conduit.xa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.projectodd.stilts.conduit.spi.MessageConduit;

public class PseudoXAResourceManager implements XAResource {
    
    public PseudoXAResourceManager(MessageConduit messageConduit) {
        this.messageConduit = messageConduit;
    }
    
    public MessageConduit getMessageConduit() {
        return this.messageConduit;
    }

    @Override
    public boolean setTransactionTimeout(int seconds) throws XAException {
        this.transactionTimeout = seconds;
        return true;
    }

    @Override
    public int getTransactionTimeout() throws XAException {
        return this.transactionTimeout;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        PseudoXATransaction tx = null;
        if (flags == XAResource.TMNOFLAGS || flags == XAResource.TMJOIN) {
            tx = new PseudoXATransaction();
            this.transactions.put( xid, tx );
        } else if (flags == XAResource.TMRESUME) {
            tx = this.transactions.get( xid );
        }

        if (tx == null) {
            throw new XAException( "Unable to start transaction: " + xid );
        }

        this.currentTransaction.set( tx );
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        PseudoXATransaction tx = this.transactions.get( xid );
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }

        if (flags == XAResource.TMFAIL) {
            tx.setRollbackOnly( true );
        }
        
        this.currentTransaction.remove();
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        PseudoXATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        if ( tx.isRollbackOnly() ) {
            throw new XAException(XAException.XA_RBROLLBACK); 
        }
        
        return XAResource.XA_OK;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        PseudoXATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.commit( this.messageConduit );
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        PseudoXATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.rollback( this.messageConduit );
    }

    @Override
    public void forget(Xid xid) throws XAException {
    }

    @Override
    public boolean isSameRM(XAResource xares) throws XAException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return null;
    }
    
    PseudoXATransaction currentTransaction() {
        return currentTransaction.get();
    }

    private final ThreadLocal<PseudoXATransaction> currentTransaction = new ThreadLocal<PseudoXATransaction>();
    private final Map<Xid, PseudoXATransaction> transactions = new ConcurrentHashMap<Xid, PseudoXATransaction>();

    private MessageConduit messageConduit;
    private int transactionTimeout;

}
