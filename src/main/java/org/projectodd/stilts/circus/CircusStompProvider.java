package org.projectodd.stilts.circus;

import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.helpers.OpenAuthenticator;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Authenticator;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompProvider;

public class CircusStompProvider implements StompProvider {
    
    public CircusStompProvider(TransactionManager transactionManager, XAMessageConduitFactory messageConduitFactory) {
        this( transactionManager, messageConduitFactory, null );
    }
    
    public CircusStompProvider(TransactionManager transactionManager, XAMessageConduitFactory messageConduitFactory, Authenticator authenticator) {
        this.transactionManager = transactionManager;
        if ( authenticator == null ) {
            authenticator = OpenAuthenticator.INSTANCE;
        }
        this.authenticator = authenticator;
        this.messageConduitFactory = messageConduitFactory;
    }
    
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    @Override
    public StompConnection createConnection(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException {
        if ( this.authenticator.authenticate( headers ) ) {
            try {
                return createStompConnection( messageSink, getNextSessionId(), headers );
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
        return null;
    }
    
    protected String getNextSessionId() {
        return "session-" + sessionCounter.getAndIncrement();
    }

    protected StompConnection createStompConnection(AcknowledgeableMessageSink messageSink, String sessionId, Headers headers) throws Exception {
        return new CircusStompConnection( this, this.messageConduitFactory.createXAMessageConduit(  messageSink ), sessionId );
    }
    
    XAMessageConduitFactory getMessageConduitFactory() {
        return this.messageConduitFactory;
    }
    
    private XAMessageConduitFactory messageConduitFactory;
    private TransactionManager transactionManager;
    private Authenticator authenticator;
    private AtomicInteger sessionCounter = new AtomicInteger();

}
