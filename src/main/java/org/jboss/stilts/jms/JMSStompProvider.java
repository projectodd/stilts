package org.jboss.stilts.jms;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.base.AbstractStompProvider;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;

public class JMSStompProvider extends AbstractStompProvider<JMSClientAgent> {
    
    public JMSStompProvider() {
        
    }
    
    public JMSStompProvider(Authenticator authenticator, DestinationMapper destinationMapper) {
        super( authenticator );
        this.destinationMapper = destinationMapper;
    }
    
    public void start() throws JMSException {
        this.connection.start();
    }
    
    public void stop() throws JMSException {
        this.connection.stop();
    }
    
    public void setDestinationMapper(DestinationMapper destinationMapper) {
        this.destinationMapper = destinationMapper;
    }
    
    public DestinationMapper getDestinationMapper() {
        return this.destinationMapper;
    }
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    protected ClientAgent createClientAgent(MessageSink messageSink, String sessionId, Headers headers) throws Exception {
        return new JMSClientAgent(this, messageSink, sessionId );
    }

    private DestinationMapper destinationMapper;
    private Connection connection;
}
