package org.jboss.stilts.stomplet;

import javax.jms.JMSException;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractStompServer;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;

public class StompletServer extends AbstractStompServer<StompletClientAgent> {
    
    public StompletServer() {
        
    }
    
    public StompletServer(Authenticator authenticator) {
        super( authenticator );
    }
    
    public void start() throws JMSException {
    }
    
    public void stop() throws JMSException {
    }
    
    @Override
    protected ClientAgent createClientAgent(MessageSink messageSink, String sessionId, Headers headers) throws Exception {
        return new StompletClientAgent(this, messageSink, sessionId );
    }
    
    StompletMessageRouter getMessageRouter() {
        return this.messageRouter;
    }
    
    // ----
    
    void send(StompMessage message) throws StompException {
        this.messageRouter.send( message );
    }
    
    void subscribe(String destination) throws StompException {
        
    }
    
    private StompletMessageRouter messageRouter = new StompletMessageRouter();

}
