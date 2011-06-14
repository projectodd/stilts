package org.jboss.stilts.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;

public class DefaultClientTransaction implements ClientTransaction {

    public DefaultClientTransaction(AbstractStompClient client, String id) {
        this( client, id, false );
    }

    public DefaultClientTransaction(AbstractStompClient client, String id, boolean isGlobal) {
        this.client = client;
        this.id = id;
        this.isGlobal = isGlobal;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean isGlobal() {
        return this.isGlobal;
    }

    @Override
    public SubscriptionBuilder subscribe(String destination) {
        DefaultSubscriptionBuilder builder = new DefaultSubscriptionBuilder( this, destination );
        if (!this.isGlobal) {
            builder.withHeader( Header.TRANSACTION, this.id );
        }
        return builder;
    }

    ClientSubscription subscribe(DefaultSubscriptionBuilder builder) throws InterruptedException, ExecutionException {
        DefaultClientSubscription subscription = this.client.subscribe( builder );
        this.subscriptions.put( subscription.getId(), subscription );
        return subscription;
    }

    void unsubscribe(DefaultClientSubscription subscription) throws InterruptedException, ExecutionException {
        this.client.unsubscribe( subscription );
    }

    void messageReceived(StompMessage message) {
        String subscriptionId = message.getHeaders().get( Header.SUBSCRIPTION );
        if (subscriptionId != null) {
            DefaultClientSubscription subscription = this.subscriptions.get( subscriptionId );
            if ( subscription != null ) {
                subscription.messageReceived( message );
            }
        }
    }

    @Override
    public void send(StompMessage message) {

    }

    @Override
    public void commit() {
        // TODO Auto-generated method stub

    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub

    }

    private final Map<String, DefaultClientSubscription> subscriptions = new HashMap<String, DefaultClientSubscription>();
    private AbstractStompClient client;
    private String id;
    private boolean isGlobal;
}
