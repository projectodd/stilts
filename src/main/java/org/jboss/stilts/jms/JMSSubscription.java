package org.jboss.stilts.jms;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.DefaultHeaders;
import org.jboss.stilts.protocol.DefaultStompServerMessage;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

public class JMSSubscription implements Subscription, MessageListener {

    JMSSubscription(JMSTransaction transaction, Destination jmsDestination, String selector, String id, boolean autoAck) {
        this.transaction = transaction;
        this.jmsDestination = jmsDestination;
        this.selector = selector;
        this.id = id;
        this.autoAck = autoAck;
    }

    public String getId() {
        return this.id;
    }

    public boolean isAutoAck() {
        return this.autoAck;
    }

    public void start() throws JMSException {
        this.consumer = this.transaction.getJMSSession().createConsumer( this.jmsDestination, this.selector );
        this.consumer.setMessageListener( this );
    }

    public void stop() throws JMSException {
    }

    @Override
    public void cancel() throws StompException {
        try {
            this.consumer.close();
            this.consumer = null;
        } catch (JMSException e) {
            throw new StompException( e );
        }
    }

    @Override
    public void onMessage(Message jmsMessage) {
        try {
            StompMessage stompMessage = translate( jmsMessage );
            if (this.autoAck) {
                jmsMessage.acknowledge();
            } else {
                this.transaction.addWaitingAcknowledger( createAcknowledger( jmsMessage ) );
            }
            this.transaction.getClientAgent().onMessage( stompMessage );
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (StompException e) {
            e.printStackTrace();
        }
    }

    JMSAcknowledger createAcknowledger(Message message) throws JMSException {
        return new JMSAcknowledger( message.getJMSMessageID(), message );
    }

    StompMessage translate(Message jmsMessage) throws JMSException {
        Headers headers = new DefaultHeaders();
        ChannelBuffer buffer = null;

        if (jmsMessage instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) jmsMessage;
            buffer = ChannelBuffers.buffer( (int) bytesMessage.getBodyLength() );
            bytesMessage.readBytes( buffer.array() );
        } else if (jmsMessage instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) jmsMessage;
            byte[] bytes = textMessage.getText().getBytes();
            buffer = ChannelBuffers.wrappedBuffer( bytes );
        }

        if (buffer != null) {
            return new DefaultStompServerMessage( headers, buffer );
        }

        return null;
    }

    private JMSTransaction transaction;
    private Destination jmsDestination;
    private String selector;
    private MessageConsumer consumer;
    private String id;
    private boolean autoAck;

}
