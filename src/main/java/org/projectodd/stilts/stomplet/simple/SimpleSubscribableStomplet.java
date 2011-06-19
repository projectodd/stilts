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

package org.projectodd.stilts.stomplet.simple;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.MessageSink;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomplet.Subscriber;
import org.projectodd.stilts.stomplet.helpers.AbstractStomplet;

public abstract class SimpleSubscribableStomplet extends AbstractStomplet implements MessageSink {

    @Override
    public void send(StompMessage message) throws StompException {
        onMessage( message );
    }

    @Override
    public void onSubscribe(Subscriber subscriber) throws StompException {
        synchronized ( this.destinations ) {
            System.err.println( "ADD SUBSCRIBER: " + subscriber );
            SubscriberList destinationSubscribers = this.destinations.get( subscriber.getDestination() );
            if ( destinationSubscribers == null ) {
                destinationSubscribers = new SubscriberList();
                this.destinations.put(  subscriber.getDestination(), destinationSubscribers );
            }
            destinationSubscribers.addSubscriber( subscriber );
        }
    }

    @Override
    public void onUnsubscribe(Subscriber subscriber) throws StompException {
        synchronized ( this.destinations ) {
            SubscriberList destinationSubscribers = this.destinations.get( subscriber.getDestination() );
            if ( destinationSubscribers != null ) {
                destinationSubscribers.removeSubscriber( subscriber );
            }
        }
    }
    
    protected void sendToAllSubscribers(StompMessage message) throws StompException {
        synchronized ( this.destinations ) {
            System.err.println( this.destinations );
            SubscriberList destinationSubscribers = this.destinations.get( message.getDestination() );
            if ( destinationSubscribers != null ) {
                destinationSubscribers.sendToAllSubscribers( message );
            }
        }
    }
    
    protected void sendToOneSubscriber(StompMessage message) throws StompException {
        synchronized ( this.destinations ){
            SubscriberList destinationSubscribers = this.destinations.get( message.getDestination() );
            if ( destinationSubscribers != null ) {
                destinationSubscribers.sendToOneSubscriber( message );
            }
        }
    }
    
    private Map<String,SubscriberList> destinations = new HashMap<String,SubscriberList>();

}
