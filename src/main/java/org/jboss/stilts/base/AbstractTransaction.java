package org.jboss.stilts.base;

import java.util.HashMap;
import java.util.Map;

import org.jboss.stilts.InvalidMessageException;
import org.jboss.stilts.InvalidSubscriptionException;
import org.jboss.stilts.StompException;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;
import org.jboss.stilts.spi.Transaction;

public abstract class AbstractTransaction<T extends AbstractClientAgent> implements Transaction {

    public AbstractTransaction(T clientAgent) {
        this.clientAgent = clientAgent;
    }

    public T getClientAgent() {
        return this.clientAgent;
    }

    @Override
    final public synchronized void ack(String messageId) throws StompException {
        Acknowledger acknowledger = this.acknowledgers.remove( messageId );
        if (acknowledger == null) {
            throw new InvalidMessageException( messageId );
        }
        try {
            acknowledger.acknowledge();
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    final public Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException {
        try {
            Subscription subscription = createSubscription( destination, subscriptionId, headers );
            if (subscription == null) {
                return null;
            }

            this.subscriptions.put( subscription.getId(), subscription );

            return subscription;
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    final public void unsubscribe(String subscriptionId) throws StompException {
        Subscription subscription = this.subscriptions.remove( subscriptionId );
        if (subscription == null) {
            throw new InvalidSubscriptionException( subscriptionId );
        }

        subscription.cancel();
    }

    public abstract Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws Exception;

    public synchronized void addWaitingAcknowledger(Acknowledger acknowledger) {
        this.acknowledgers.put( acknowledger.getId(), acknowledger );
    }

    private T clientAgent;

    private Map<String, Acknowledger> acknowledgers = new HashMap<String, Acknowledger>();
    private Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();

}
