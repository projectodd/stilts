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

package org.projectodd.stilts.stomplet.container;

import java.util.concurrent.atomic.AtomicLong;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.StompSession;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class SubscriberImpl implements Subscriber {

    public SubscriberImpl(StompSession session, Stomplet stomplet, String subscriptionId, String destination, AcknowledgeableMessageSink messageSink, AckMode ackMode) {
        this.session = session;
        this.stomplet = stomplet;
        this.subscriptionId = subscriptionId;
        this.destination = destination;
        this.messageSink = messageSink;
        //this.messageConduit = messageConduit;
        this.ackMode = ackMode;

        if (this.ackMode == AckMode.CLIENT) {
            this.ackSet = new CumulativeAckSet();
        } else if (this.ackMode == AckMode.CLIENT_INDIVIDUAL) {
            this.ackSet = new IndividualAckSet();
        } else {
            this.ackMode = AckMode.AUTO;
        }
    }

    
    public String getId() {
        return this.session.getId() + "|" + this.subscriptionId;
    }
    
    @Override
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public StompSession getSession() {
        return this.session;
    }

    public AckMode getAckMode() {
        return this.ackMode;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        send( message, null );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        StompMessage dupe = message.duplicate();
        dupe.getHeaders().put( Header.SUBSCRIPTION, this.subscriptionId );
        dupe.getHeaders().put( Header.MESSAGE_ID, getNextMessageId() );
        dupe.getHeaders().put( Header.DESTINATION, this.destination );

        if ((acknowledger == null) && (this.stomplet instanceof AcknowledgeableStomplet)) {
            acknowledger = new StompletAcknowledger( (AcknowledgeableStomplet) this.stomplet, this, dupe );
        }

        if (this.ackMode == AckMode.AUTO) {
            if (acknowledger != null) {
                try {
                    acknowledger.ack();
                } catch (Exception e) {
                    throw new StompException( e );
                }
            }
            this.messageSink.send( dupe );
        } else {
            if ( acknowledger == null ) {
                acknowledger = new NoOpAcknowledger();
            }
            this.ackSet.addAcknowledger( dupe.getId(), acknowledger );
            SubscriberAcknowledger subscriberAcknowledger = new SubscriberAcknowledger( this, dupe.getId() );
            this.messageSink.send( dupe, subscriberAcknowledger );
        }
    }

    void ack(String messageId) throws Exception {
        this.ackSet.ack( messageId );
    }

    void nack(String messageId) throws Exception {
        this.ackSet.nack( messageId );
    }

    void close() {
        if (ackSet != null) {
            this.ackSet.close();
        }
    }

    @Override
    public String getDestination() {
        return this.destination;
    }

    protected String getNextMessageId() {
        return this.subscriptionId + "-message-" + messageCounter.getAndIncrement();
    }

    private AtomicLong messageCounter = new AtomicLong();
    private StompSession session;
    private Stomplet stomplet;
    //private StompletMessageConduit messageConduit;
    private AcknowledgeableMessageSink messageSink;
    private String subscriptionId;
    private String destination;
    private AckMode ackMode;
    private AckSet ackSet;

    @Override
    public String toString() {
        return "Subscriber [id=" + subscriptionId + ", destination=" + destination + ", ackMode=" + ackMode + "]";
    }

}