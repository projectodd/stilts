package org.jboss.stilts.stomplet.simple;

import java.util.HashMap;
import java.util.Map;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.stomplet.Subscriber;
import org.jboss.stilts.stomplet.helpers.AbstractStomplet;

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
