package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.base.AbstractTransaction;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;

public class StompletTransaction extends AbstractTransaction<StompletClientAgent> {

    public StompletTransaction(StompletClientAgent clientAgent, String transactionId) {
        super( clientAgent );
    }

    @Override
    public void commit() throws StompException {

    }

    @Override
    public void abort() throws StompException {

    }

    @Override
    public Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws StompException {
        RouteMatch match = getClientAgent().getServer().getMessageRouter().match( destination );
        if (match == null) {
            return null;
        }

        MessageSink subscriber = getClientAgent().getMessageSink();
        Stomplet stomplet = match.getRoute().getStomplet();
        stomplet.onSubscribe( subscriber );
        return new StompletSubscription( stomplet, subscriber );
    }

    @Override
    public void close() throws StompException {

    }

    @Override
    public void send(StompMessage message) throws StompException {
        getClientAgent().getServer().send( message );
    }

}
