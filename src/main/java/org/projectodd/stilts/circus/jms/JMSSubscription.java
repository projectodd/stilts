/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.circus.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.CircusSubscription;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

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
