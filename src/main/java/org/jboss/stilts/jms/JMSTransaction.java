package org.jboss.stilts.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractTransaction;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Headers;

public class JMSTransaction extends AbstractTransaction<JMSClientAgent> {

    JMSTransaction(JMSClientAgent clientAgent, Session session, String id) {
        super( clientAgent );
        this.session = session;
        this.id = id;
    }

    Session getJMSSession() {
        return this.session;
    }

    public void send(StompMessage message) throws StompException {
        try {
            Destination jmsDestination = getClientAgent().getServer().getDestinationMapper().map( session, message.getDestination() );
            MessageProducer producer = this.session.createProducer( jmsDestination );
            Message jmsMessage = translate( message );
            producer.send( jmsMessage );
            producer.close();
        } catch (JMSException e) {
            throw new StompException( e );
        }

    }
    
    protected Message translate(StompMessage message) throws JMSException {
        TextMessage jmsMessage = this.session.createTextMessage();
        jmsMessage.setText( message.getContentAsString() );
        return jmsMessage;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public void commit() throws StompException {
        try {
            this.session.commit();
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

    @Override
    public void abort() throws StompException {
        try {
            this.session.rollback();
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

    @Override
    public JMSSubscription createSubscription(String destinationName, String subscriptionId, Headers headers) throws JMSException {
        boolean autoAck = true;
        
        String ackHeader = headers.get(  Header.ACK  );
        
        if ( "client".equals( ackHeader ) || "client-individual".equals( ackHeader ) ) {
            autoAck = false;
        }
        
        DestinationSpec destinationSpec = getClientAgent().getServer().getDestinationMapper().map( this.session, destinationName, headers );
        Destination jmsDestination = destinationSpec.getDestination();
        String selector = destinationSpec.getSelector();
        JMSSubscription subscription = new JMSSubscription( this, jmsDestination, selector, subscriptionId, autoAck );
        subscription.start();
        return subscription;
    }
    
    public void close() throws StompException {
        try {
            this.session.close();
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

    private final Session session;
    private final String id;

}
