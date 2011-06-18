package org.jboss.stilts.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractSubscription;

public class JMSSubscription extends AbstractSubscription<JMSClientAgent> implements MessageListener {

    private MessageConsumer consumer;

    public JMSSubscription(JMSClientAgent clientAgent, String id, Destination destination, String selector) throws JMSException {
        super( clientAgent, id );
        this.consumer = clientAgent.getJMSSession().createConsumer( destination, selector );
        this.consumer.setMessageListener( this );
    }

    @Override
    public void onMessage(Message jmsMessage) {
        StompMessage stompMessage = null;
        try {
            getClientAgent().onMessage( stompMessage, new JMSMessageAcknowledger( jmsMessage ) );
        } catch (StompException e) {
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
