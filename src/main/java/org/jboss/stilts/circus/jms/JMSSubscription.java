package org.jboss.stilts.circus.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.circus.CircusSubscription;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

public class JMSSubscription extends CircusSubscription implements MessageListener {

    private MessageConsumer consumer;
    private String stompDestination;
    private AcknowledgeableMessageSink messageSink;

    public JMSSubscription(String id, String stompDestination, MessageConsumer consumer, AcknowledgeableMessageSink messageSink) throws JMSException {
        super( id );
        this.stompDestination = stompDestination;
        this.consumer = consumer;
        this.messageSink = messageSink;
        this.consumer.setMessageListener( this );
    }

    @Override
    public void onMessage(Message jmsMessage) {
        System.err.println( "ON JMS: " +jmsMessage );
        StompMessage stompMessage;
        try {
            stompMessage = MessageConverter.convert( jmsMessage );
            stompMessage.setDestination( this.stompDestination );
            stompMessage.getHeaders().put( Header.SUBSCRIPTION, getId() );
            this.messageSink.send( stompMessage, new JMSMessageAcknowledger( jmsMessage ) );
        } catch (StompException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel() throws StompException {
        try {
            this.consumer.close();
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

}
