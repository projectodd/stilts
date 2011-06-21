/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
