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

package org.projectodd.stilts.stomplet.conduit;

import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;
import org.projectodd.stilts.stomplet.container.DefaultSubscriber;
import org.projectodd.stilts.stomplet.container.RouteMatch;
import org.projectodd.stilts.stomplet.container.StompletContainer;
import org.projectodd.stilts.stomplet.stomp.StompletSubscription;

public class StompletMessageConduit implements MessageConduit {

    public StompletMessageConduit(StompletContainer stompletContainer, AcknowledgeableMessageSink messageSink) throws StompException {
        this.stompletContainer = stompletContainer;
        this.messageSink = messageSink;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.stompletContainer.send( message );
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        RouteMatch match = this.stompletContainer.match( destination );
        if (match == null) {
            return null;
        }
        
        Stomplet stomplet = match.getRoute().getStomplet();
        
        String ackHeader = headers.get( Header.ACK );
        
        AckMode ackMode = AckMode.AUTO;
        
        if ( ackHeader == null || "auto".equalsIgnoreCase( ackHeader ) ) {
            ackMode = AckMode.AUTO;
        } else if ( "client".equalsIgnoreCase( ackHeader ) ){
            ackMode = AckMode.CLIENT;
        } else if ( "client-individual".equalsIgnoreCase( ackHeader ) ){
            ackMode = AckMode.CLIENT_INDIVIDUAL;
        }
        
        Subscriber subscriber = new DefaultSubscriber( stomplet, subscriptionId, destination, this.messageSink, ackMode );
        stomplet.onSubscribe( subscriber );
        return new StompletSubscription( stomplet, subscriber );
    }
    
    private StompletContainer stompletContainer;
    private AcknowledgeableMessageSink messageSink;


}
