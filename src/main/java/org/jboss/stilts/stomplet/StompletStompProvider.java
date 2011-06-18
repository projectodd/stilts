package org.jboss.stilts.stomplet;

import javax.transaction.TransactionManager;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractStompProvider;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;

public class StompletStompProvider extends AbstractStompProvider {

    public StompletStompProvider(TransactionManager transactionManager, StompletContainer stompletContainer) {
        super( transactionManager );
        this.stompletContainer = stompletContainer;
    }

    public void start() throws StompException {
        this.stompletContainer.start();
    }

    public void stop() throws StompException {
        this.stompletContainer.stop();
    }

    @Override
    protected ClientAgent createClientAgent(TransactionManager transactionManager, AcknowledgeableMessageSink messageSink, String sessionId, Headers headers) throws Exception {
        if (getXAStompProvider() != null) {
            return new StompletClientAgent( this.stompletContainer, transactionManager, getXAStompProvider(), messageSink, sessionId );
        } else {
            return new StompletClientAgent( this.stompletContainer, transactionManager, this, messageSink, sessionId );
        }
    }

    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        this.stompletContainer.send( message );
    }
    
    // ----

    private StompletContainer stompletContainer;

}
