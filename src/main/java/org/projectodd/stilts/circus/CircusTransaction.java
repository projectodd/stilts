package org.projectodd.stilts.circus;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.StompTransaction;

public class CircusTransaction implements StompTransaction {

    public CircusTransaction(CircusStompConnection clientAgent, Transaction transaction, String id) {
        this.stompConnection = clientAgent;
        this.transaction = transaction;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public CircusStompConnection getStompConnection() {
        return this.stompConnection;
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
            TransactionManager tm = this.stompConnection.getStompProvider().getTransactionManager();
            tm.resume( this.transaction );
            XAResource xaResource = this.stompConnection.getMessageConduit().getXAResource();
            this.transaction.enlistResource( xaResource );
            message.getHeaders().remove( Header.TRANSACTION );
            this.stompConnection.send( message );
            tm.suspend();
        } catch (StompException e) {
            throw e;
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    public void ack(Acknowledger acknowledger) throws StompException {
        try {
            TransactionManager tm = this.stompConnection.getStompProvider().getTransactionManager();
            tm.resume( this.transaction );
            XAResource xaResource = this.stompConnection.getMessageConduit().getXAResource();
            this.transaction.enlistResource( xaResource );
            acknowledger.ack();
            tm.suspend();
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    public void nack(Acknowledger acknowledger) throws StompException {
        try {
            TransactionManager tm = this.stompConnection.getStompProvider().getTransactionManager();
            tm.resume( this.transaction );
            XAResource xaResource = this.stompConnection.getMessageConduit().getXAResource();
            this.transaction.enlistResource( xaResource );
            acknowledger.nack();
            tm.suspend();
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
    private CircusStompConnection stompConnection;
    private String id;
}
