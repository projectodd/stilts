package org.jboss.stilts.base;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompProvider;

public abstract class AbstractStompProvider<T extends AbstractClientAgent> implements StompProvider {
    
    public AbstractStompProvider() {
        this( OpenAuthenticator.INSTANCE );
    }
    
    public AbstractStompProvider(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    @Override
    public ClientAgent connect(MessageSink messageSink, Headers headers) throws StompException {
        if ( this.authenticator.authenticate( headers ) ) {
            try {
                return createClientAgent( messageSink, getNextSessionId(), headers );
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
        return null;
    }
    
    protected String getNextSessionId() {
        return "session-" + sessionCounter.getAndIncrement();
    }

    protected abstract ClientAgent createClientAgent(MessageSink messageSink, String sessionId, Headers headers) throws Exception;
    
    private Authenticator authenticator;
    private AtomicInteger sessionCounter = new AtomicInteger();

}
