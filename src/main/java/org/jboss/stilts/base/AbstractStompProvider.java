package org.jboss.stilts.base;

import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.TransactionManager;

import org.jboss.stilts.StompException;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.spi.XAStompProvider;

public abstract class AbstractStompProvider implements StompProvider {
    
    public AbstractStompProvider(TransactionManager transactionManager) {
        this( transactionManager, OpenAuthenticator.INSTANCE );
    }
    
    public AbstractStompProvider(TransactionManager transactionManager, Authenticator authenticator) {
        this.transactionManager = transactionManager;
        this.authenticator = authenticator;
    }
    
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setXAStompProvider(XAStompProvider xaStompProvider) {
        this.xaStompProvider = xaStompProvider;
    }
    
    public XAStompProvider getXAStompProvider() {
        return this.xaStompProvider;
    }

    @Override
    public ClientAgent connect(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException {
        if ( this.authenticator.authenticate( headers ) ) {
            try {
                return createClientAgent( this.transactionManager, messageSink, getNextSessionId(), headers );
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
        return null;
    }
    
    protected String getNextSessionId() {
        return "session-" + sessionCounter.getAndIncrement();
    }

    protected abstract ClientAgent createClientAgent(TransactionManager transactionManager, AcknowledgeableMessageSink messageSink, String sessionId, Headers headers) throws Exception;
    
    private XAStompProvider xaStompProvider;
    private TransactionManager transactionManager;
    private Authenticator authenticator;
    private AtomicInteger sessionCounter = new AtomicInteger();

}
