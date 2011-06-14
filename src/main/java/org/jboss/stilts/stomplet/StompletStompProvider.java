package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.base.AbstractStompProvider;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;

public class StompletStompProvider extends AbstractStompProvider<StompletClientAgent> {
    
    public StompletStompProvider(StompletContainer stompletContainer) {
        this.stompletContainer = stompletContainer;
    }
    
    public StompletStompProvider(Authenticator authenticator) {
        super( authenticator );
    }
    
    public void start() throws StompException {
        this.stompletContainer.start();
    }
    
    public void stop() throws StompException {
        this.stompletContainer.stop();
    }
    
    @Override
    protected ClientAgent createClientAgent(MessageSink messageSink, String sessionId, Headers headers) throws Exception {
        return new StompletClientAgent(this, messageSink, sessionId );
    }
    
    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }
    
    // ----
    
    private ClassLoader classLoader;

    private StompletContainer stompletContainer;

}
