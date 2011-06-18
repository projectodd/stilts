package org.jboss.stilts.xa;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jboss.stilts.spi.StompProvider;

public class StompProviderResourceManager implements XAResource {
    
    public StompProviderResourceManager(StompProvider provider) {
        this.provider = provider;
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
        XATransaction tx = null;
        System.err.println( "START: " + xid + " // " + flags );
        if (flags == XAResource.TMNOFLAGS || flags == XAResource.TMJOIN) {
            tx = new XATransaction();
            this.transactions.put( xid, tx );
        } else if (flags == XAResource.TMRESUME) {
            tx = this.transactions.get( xid );
        }

        if (tx == null) {
            throw new XAException( "Unable to start transaction: " + xid );
        }

        System.err.println( "SET CURRENT TX: " + tx );
        CURRENT_TRANSACTION.set( tx );
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        XATransaction tx = this.transactions.get( xid );
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }

        if (flags == XAResource.TMFAIL) {
            tx.setRollbackOnly( true );
        }
        
        CURRENT_TRANSACTION.remove();
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        XATransaction tx = this.transactions.get( xid );
        
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
        XATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.commit( this.provider );
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        XATransaction tx = this.transactions.get( xid );
        
        if (tx == null) {
            throw new XAException( "No such transaction: " + xid );
        }
        
        tx.rollback( this.provider );
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
    
    static XATransaction currentTransaction() {
        return CURRENT_TRANSACTION.get();
    }

    private static final ThreadLocal<XATransaction> CURRENT_TRANSACTION = new ThreadLocal<XATransaction>();
    private final Map<Xid, XATransaction> transactions = new ConcurrentHashMap<Xid, XATransaction>();

    private StompProvider provider;
    private int transactionTimeout;

}