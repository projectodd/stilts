package org.projectodd.stilts.conduit;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.MockSubscription;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;

public class MockMessageConduit implements MessageConduit {

    public MockMessageConduit() {
    }

    @Override
    public void send(StompMessage message) throws Exception {
        this.messages.add( message );
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        MockSubscription subscription = new MockSubscription( subscriptionId, destination, headers );
        this.subscriptions.add( subscription );
        return subscription;
    }
    
    public List<StompMessage> getMessages() {
        return this.messages;
    }
    
    public List<Subscription> getSubscriptions() {
        return this.subscriptions;
    }

    private List<StompMessage> messages = new ArrayList<StompMessage>();
    private List<Subscription> subscriptions = new ArrayList<Subscription>();

}
