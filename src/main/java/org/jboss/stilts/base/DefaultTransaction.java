package org.jboss.stilts.base;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.spi.StompTransaction;
import org.jboss.stilts.spi.XAStompProvider;

public class DefaultTransaction implements StompTransaction {

    public DefaultTransaction(AbstractClientAgent clientAgent, Transaction transaction, String id) {
        this.clientAgent = clientAgent;
        this.transaction = transaction;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public AbstractClientAgent getClientAgent() {
        return this.clientAgent;
    }

    public Transaction getJTATransaction() {
        return this.transaction;
    }

    @Override
    public void commit() throws StompException {
        try {
            this.transaction.commit();
        } catch (SecurityException e) {
            throw new StompException( e );
        } catch (IllegalStateException e) {
            throw new StompException( e );
        } catch (RollbackException e) {
            throw new StompException( e );
        } catch (HeuristicMixedException e) {
            throw new StompException( e );
        } catch (HeuristicRollbackException e) {
            throw new StompException( e );
        } catch (SystemException e) {
            throw new StompException( e );
        }
    }

    @Override
    public void abort() throws StompException {
        try {
            this.transaction.rollback();
        } catch (IllegalStateException e) {
            throw new StompException( e );
        } catch (SystemException e) {
            throw new StompException( e );
        }
    }

    @Override
    public void send(StompMessage message) throws StompException {
        try {
            this.clientAgent.getTransactionManager().resume( this.transaction );
            StompProvider provider = getClientAgent().getStompProvider();
            if ( provider instanceof XAStompProvider ) {
                XAResource xaResource = ((XAStompProvider)provider).getXAResource();
                this.clientAgent.getTransactionManager().getTransaction().enlistResource( xaResource );
            }
            message.getHeaders().remove( Header.TRANSACTION );
            getClientAgent().getStompProvider().send( message );
            this.clientAgent.getTransactionManager().suspend();
        } catch (StompException e) {
            throw e;
        } catch (Exception e) {
            throw new StompException( e );
        }
    }
    
    public void ack(Acknowledger acknowledger) throws StompException {
        try {
            this.clientAgent.getTransactionManager().resume( this.transaction );
            StompProvider provider = getClientAgent().getStompProvider();
            if ( provider instanceof XAStompProvider ) {
                XAResource xaResource = ((XAStompProvider)provider).getXAResource();
                this.clientAgent.getTransactionManager().getTransaction().enlistResource( xaResource );
            }
            acknowledger.ack();
            this.clientAgent.getTransactionManager().suspend();
        } catch (Exception e) {
            throw new StompException( e );
        }  
    }
    
    public void nack(Acknowledger acknowledger) throws StompException {
        try {
            this.clientAgent.getTransactionManager().resume( this.transaction );
            StompProvider provider = getClientAgent().getStompProvider();
            if ( provider instanceof XAStompProvider ) {
                XAResource xaResource = ((XAStompProvider)provider).getXAResource();
                this.clientAgent.getTransactionManager().getTransaction().enlistResource( xaResource );
            }
            acknowledger.ack();
            this.clientAgent.getTransactionManager().suspend();
        } catch (Exception e) {
            throw new StompException( e );
        }  
    }


    
    public String toString() {
        return "[" + getClass().getSimpleName() + ": " + id + "]";
    }
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private Transaction transaction;
    private AbstractClientAgent clientAgent;
    private String id;
}
