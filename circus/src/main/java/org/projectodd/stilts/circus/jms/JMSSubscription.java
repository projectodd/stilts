/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.circus.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.CircusSubscription;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

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
