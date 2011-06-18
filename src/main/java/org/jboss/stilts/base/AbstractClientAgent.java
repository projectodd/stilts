package org.jboss.stilts.base;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.stilts.InvalidSubscriptionException;
import org.jboss.stilts.InvalidTransactionException;
import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.spi.StompTransaction;
import org.jboss.stilts.spi.Subscription;

public abstract class AbstractClientAgent implements ClientAgent {

    public AbstractClientAgent(TransactionManager transactionManager, StompProvider stompProvider, AcknowledgeableMessageSink messageSink, String sessionId)
            throws StompException {
        this.transactionManager = transactionManager;
        this.stompProvider = stompProvider;
        this.messageSink = messageSink;
        this.sessionId = sessionId;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    public AcknowledgeableMessageSink getMessageSink() {
        return this.messageSink;
    }

    public StompProvider getStompProvider() {
        return this.stompProvider;
    }

    public void send(StompMessage message, String transactionId) throws StompException {
        if (transactionId != null) {
            System.err.println( "ClientAgent.send to transaction: " + transactionId );
            getTransaction( transactionId ).send( message );
        } else {
            System.err.println( "ClientAgent.send to provider" );
            getStompProvider().send( message );
        }
    }

    @Override
    public void ack(Acknowledger acknowledger, String transactionId) throws StompException {
        if (transactionId != null) {
            System.err.println( "ClientAgent.ack to transaction: " + transactionId );
            getTransaction( transactionId ).ack( acknowledger );
        } else {
            System.err.println( "ClientAgent.ack to provider" );
            try {
                acknowledger.ack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
    }

    @Override
    public void nack(Acknowledger acknowledger, String transactionId) throws StompException {
        if (transactionId != null) {
            System.err.println( "ClientAgent.send to transaction: " + transactionId );
            getTransaction( transactionId ).nack( acknowledger );
        } else {
            System.err.println( "ClientAgent.send to provider" );
            try {
                acknowledger.nack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
    }

    public void onMessage(StompMessage message, Acknowledger acknowledger) throws StompException {
        getMessageSink().send( message, acknowledger );
    }

    DefaultTransaction getTransaction(String transactionId) throws InvalidTransactionException {

        DefaultTransaction transaction = this.namedTransactions.get( transactionId );

        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }

        return transaction;
    }

    DefaultTransaction removeTransaction(String transactionId) {
        return this.namedTransactions.remove( transactionId );
    }

    @Override
    public void begin(String transactionId, Headers headers) throws StompException {

        Transaction jtaTransaction = null;
        try {
            this.transactionManager.begin();
            jtaTransaction = this.transactionManager.getTransaction();
            this.transactionManager.suspend();
        } catch (NotSupportedException e) {
            throw new StompException( e );
        } catch (SystemException e) {
            throw new StompException( e );
        }

        try {
            DefaultTransaction transaction = createTransaction( jtaTransaction, transactionId );
            System.err.println( "NAMED TX: " + transaction );
            this.namedTransactions.put( transactionId, transaction );
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public void commit(String transactionId) throws StompException {
        StompTransaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        transaction.commit();
    }

    @Override
    public void abort(String transactionId) throws StompException {
        StompTransaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        transaction.abort();
    }

    @Override
    public synchronized Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException {
        try {
            Subscription subscription = createSubscription( destination, subscriptionId, headers );
            if (subscription == null) {
                return null;
            }
            this.subscriptions.put( subscription.getId(), subscription );
            return subscription;
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    public abstract Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws Exception;

    @Override
    public synchronized void unsubscribe(String id, Headers headers) throws StompException {
        Subscription subscription = this.subscriptions.remove( id );
        if (subscription == null) {
            throw new InvalidSubscriptionException( id );
        }
    }

    @Override
    public synchronized void disconnect() throws NotConnectedException {
        for (StompTransaction each : this.namedTransactions.values()) {
            try {
                each.abort();
            } catch (StompException e) {
                e.printStackTrace();
            }
        }
    }

    protected DefaultTransaction createTransaction(Transaction jtaTransaction, String transactionId) throws Exception {
        return new DefaultTransaction( this, jtaTransaction, transactionId );
    }

    private Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();

    private Map<String, DefaultTransaction> namedTransactions = new HashMap<String, DefaultTransaction>();

    private TransactionManager transactionManager;
    private StompProvider stompProvider;
    private String sessionId;
    private AcknowledgeableMessageSink messageSink;

}
