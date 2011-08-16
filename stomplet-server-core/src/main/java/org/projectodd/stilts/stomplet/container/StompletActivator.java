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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.XAStomplet;

public class StompletActivator {

    public StompletActivator(Route route, String destination, Map<String, String> matches) {
        this.route = route;
        this.destination = destination;
        this.matches = matches;
    }

    public Route getRoute() {
        return this.route;
    }

    public String getDestination() {
        return this.destination;
    }

    public void put(String name, String value) {
        this.matches.put( name, value );
    }

    public String get(String name) {
        return this.matches.get( name );
    }

    public Map<String, String> getMatches() {
        return this.matches;
    }

    public Set<XAResource> getXAResources() {
        Stomplet stomplet = getRoute().getStomplet();
        if (stomplet instanceof XAStomplet) {
            return ((XAStomplet) stomplet).getXAResources();
        }

        return Collections.emptySet();
    }

    public void send(StompMessage message) throws StompException {
        this.route.getStomplet().onMessage( message );
    }

    public Subscription subscribe(StompletMessageConduit messageConduit, String subscriptionId, String destination, Headers headers) throws StompException {
        Stomplet stomplet = getRoute().getStomplet();
        AckMode ackMode = AckMode.getAckMode( headers.get( Header.ACK ) );
        SubscriberImpl subscriber = new SubscriberImpl( messageConduit.getSession(), stomplet, subscriptionId, destination, messageConduit.getMessageSink(), ackMode );
        stomplet.onSubscribe( subscriber );
        Subscription subscription = new SubscriptionImpl( stomplet, subscriber );
        return subscription;
    }

    protected Route route;
    protected String destination;
    protected Map<String, String> matches;

}
