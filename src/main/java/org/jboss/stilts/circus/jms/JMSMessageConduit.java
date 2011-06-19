package org.jboss.stilts.circus.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.circus.xa.XAMessageConduit;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

public class JMSMessageConduit implements XAMessageConduit {

    public JMSMessageConduit(XASession session, AcknowledgeableMessageSink messageSink, DestinationMapper destinationMapper) {
        this.session = session;
        this.messageSink = messageSink;
        this.destinationMapper = destinationMapper;
    }

    @Override
    public XAResource getXAResource() {
        return this.session.getXAResource();
    }

    XASession getJMSSession() {
        return this.session;
    }

    @Override
    public void send(StompMessage stompMessage) throws StompException {
        try {
            Destination jmsDestination = this.destinationMapper.map( this.session, stompMessage.getDestination() );
            Message jmsMessage = MessageConverter.convert( this.session, stompMessage );
            MessageProducer producer = this.session.createProducer( jmsDestination );
            producer.send( jmsMessage  );
            producer.close();
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        Destination jmsDestination = this.destinationMapper.map( this.session, destination );
        String selector = headers.get( Header.SELECTOR );

        MessageConsumer consumer = this.session.createConsumer( jmsDestination, selector );
        System.err.println( "JMS CONSUMER: " + jmsDestination + " // " + consumer );
        return new JMSSubscription( subscriptionId, destination, consumer, messageSink );
    }

    private XASession session;
    private DestinationMapper destinationMapper;
    private AcknowledgeableMessageSink messageSink;

}
