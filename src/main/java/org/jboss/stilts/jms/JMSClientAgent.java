package org.jboss.stilts.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.TransactionManager;

import org.jboss.stilts.StompException;
import org.jboss.stilts.base.AbstractClientAgent;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

public class JMSClientAgent extends AbstractClientAgent {

    public JMSClientAgent(TransactionManager transactionManager, JMSStompProvider stompProvider, AcknowledgeableMessageSink messageSink, String sessionId) throws StompException, JMSException {
        super( transactionManager, stompProvider, messageSink, sessionId );
        this.session = stompProvider.getConnection().createSession( false, Session.CLIENT_ACKNOWLEDGE );
    }
    
    Session getJMSSession() {
        return this.session;
    }

    @Override
    public Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws Exception {
        Destination jmsDestination = null;
        String selector = headers.get( Header.SELECTOR );
        return new JMSSubscription( this, subscriptionId, jmsDestination, selector );
    }
    
    private Session session;
}
