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

package org.projectodd.stilts.stomplet.container.xa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jboss.logging.Logger;
import org.projectodd.stilts.stomplet.Stomplet;

public class PseudoXAStompletResourceManager implements XAResource {
    
    public PseudoXAStompletResourceManager(Stomplet stomplet) {
        this.stomplet = stomplet;
    }
    
    public Stomplet getStomplet() {
        return this.stomplet;
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
        log.info( "start(" + xid + ", " + flags + ")" );
        PseudoXAStompletTransaction tx = null;
        if (flags == XAResource.TMNOFLAGS || flags == XAResource.TMJOIN) {
            tx = new PseudoXAStompletTransaction( this.stomplet );
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
        log.info( "end(" + xid + ", " + flags + ")" );
        PseudoXAStompletTransaction tx = this.transactions.get( xid );
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
        log.info( "prepare(" + xid + ")" );
        PseudoXAStompletTransaction tx = this.transactions.get( xid );
        
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
        log.info( "commit(" + xid + "," + onePhase + ")" );
        PseudoXAStompletTransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.commit();
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        log.info( "rollback(" + xid + ")" );
        PseudoXAStompletTransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.rollback();
    }

    @Override
    public void forget(Xid xid) throws XAException {
        log.info( "forget(" + xid + ")" );
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
    
    public PseudoXAStompletTransaction currentTransaction() {
        return currentTransaction.get();
    }
    
    private static final Logger log = Logger.getLogger( "stilts.stomplet.xa.pseudo.rm" );

    private final ThreadLocal<PseudoXAStompletTransaction> currentTransaction = new ThreadLocal<PseudoXAStompletTransaction>();
    private final Map<Xid, PseudoXAStompletTransaction> transactions = new ConcurrentHashMap<Xid, PseudoXAStompletTransaction>();

    private Stomplet stomplet;
    private int transactionTimeout;

}
