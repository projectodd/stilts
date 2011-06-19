package org.jboss.stilts.circus.xa.psuedo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jboss.stilts.circus.MessageConduit;

public class PsuedoXAResourceManager implements XAResource {
    
    public PsuedoXAResourceManager(MessageConduit messageConduit) {
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
        PsuedoXATransaction tx = null;
        System.err.println( "START: " + xid + " // " + flags );
        if (flags == XAResource.TMNOFLAGS || flags == XAResource.TMJOIN) {
            tx = new PsuedoXATransaction();
            this.transactions.put( xid, tx );
        } else if (flags == XAResource.TMRESUME) {
            tx = this.transactions.get( xid );
        }

        if (tx == null) {
            throw new XAException( "Unable to start transaction: " + xid );
        }

        System.err.println( "SET CURRENT TX: " + tx );
        this.currentTransaction.set( tx );
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        PsuedoXATransaction tx = this.transactions.get( xid );
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
        PsuedoXATransaction tx = this.transactions.get( xid );
        
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
        PsuedoXATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.commit( this.messageConduit );
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        PsuedoXATransaction tx = this.transactions.get( xid );
        
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
    
    PsuedoXATransaction currentTransaction() {
        return currentTransaction.get();
    }

    private final ThreadLocal<PsuedoXATransaction> currentTransaction = new ThreadLocal<PsuedoXATransaction>();
    private final Map<Xid, PsuedoXATransaction> transactions = new ConcurrentHashMap<Xid, PsuedoXATransaction>();

    private MessageConduit messageConduit;
    private int transactionTimeout;

}