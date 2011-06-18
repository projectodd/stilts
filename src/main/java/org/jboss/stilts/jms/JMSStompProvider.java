package org.jboss.stilts.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.transaction.TransactionManager;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractStompProvider;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.Headers;

public class JMSStompProvider extends AbstractStompProvider {
    
    public JMSStompProvider(TransactionManager transactionManager) {
        super( transactionManager );
    }
    
    public JMSStompProvider(TransactionManager transactionManager, Authenticator authenticator) {
        super( transactionManager, authenticator );
    }
    
    public JMSStompProvider(TransactionManager transactionManager, Authenticator authenticator, DestinationMapper destinationMapper) {
        super( transactionManager, authenticator );
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
    protected JMSClientAgent createClientAgent(TransactionManager transactionManager, AcknowledgeableMessageSink messageSink, String sessionId, Headers headers) throws Exception {
        return new JMSClientAgent( transactionManager, this, messageSink, sessionId );
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        // TODO Auto-generated method stub
    }

    private DestinationMapper destinationMapper;
    private Connection connection;
}
