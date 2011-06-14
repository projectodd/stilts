package org.jboss.stilts.base;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.stilts.InvalidTransactionException;
import org.jboss.stilts.MessageSink;
import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.client.DefaultSubscriptionBuilder;
import org.jboss.stilts.client.SubscriptionBuilder;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;
import org.jboss.stilts.spi.Transaction;

public abstract class AbstractClientAgent implements ClientAgent {

    public AbstractClientAgent(AbstractStompProvider<?> server, MessageSink messageSink, String sessionId) throws StompException {
        this.server = server;
        this.messageSink = messageSink;
        this.sessionId = sessionId;
        try {
            this.globalTransaction = createTransaction( sessionId + "-global" );
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    public MessageSink getMessageSink() {
        return this.messageSink;
    }

    public AbstractStompProvider<?> getServer() {
        return this.server;
    }

    protected String getNextTransactionId() {
        return getSessionId() + "-" + transactionCounter.getAndIncrement();
    }

    public void send(StompMessage message) throws StompException {
        System.err.println( "CA.send: " + message );
        getTransaction( message.getHeaders() ).send( message );
    }

    public void onMessage(StompMessage message) throws StompException {
        getMessageSink().send( message );
    }

    public void ack(String messageId, String transactionId, Headers headers) throws StompException {
        getTransaction( headers ).ack( messageId );
    }

    protected Transaction getTransaction(Headers headers)
            throws InvalidTransactionException {
        String transactionId = headers.get( Header.TRANSACTION );
        if (transactionId == null) {
            return this.globalTransaction;
        }

        Transaction transaction = this.namedTransactions.get( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }

        return transaction;
    }

    protected Transaction removeTransaction(String transactionId) {
        return this.namedTransactions.remove( transactionId );
    }

    @Override
    public void begin(String transactionId, Headers headers)
            throws StompException {
        try {
            Transaction transaction = createTransaction( getNextTransactionId() );
            this.namedTransactions.put( transactionId, transaction );
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public void commit(String transactionId, Headers headers) throws StompException {
        Transaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        try {
            transaction.commit();
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public void abort(String transactionId, Headers headers) throws StompException {
        Transaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        try {
            transaction.abort();
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public synchronized Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException {
        return getTransaction( headers ).subscribe( destination, subscriptionId, headers );
    }

    @Override
    public synchronized void unsubscribe(String id, Headers headers) throws StompException {
        getTransaction( headers ).unsubscribe( id );
    }

    @Override
    public synchronized void disconnect() throws NotConnectedException {
        for (Transaction each : this.namedTransactions.values()) {
            try {
                each.close();
            } catch (StompException e) {
                e.printStackTrace();
            }
        }

        try {
            this.globalTransaction.close();
        } catch (StompException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create an implementation-specific transaction.
     * 
     * @param transactionId The STOMP transaction ID passed by the client.
     * @return The new implementation transaction.
     * @throws Exception A native exception if an error occurs.
     */
    protected abstract Transaction createTransaction(String transactionId) throws Exception;

    private Transaction globalTransaction;
    private Map<String, Transaction> namedTransactions = new HashMap<String, Transaction>();

    private AbstractStompProvider<?> server;
    private String sessionId;
    private MessageSink messageSink;
    private AtomicInteger transactionCounter = new AtomicInteger( 1 );

}
