package org.jboss.stilts.client;

import org.jboss.stilts.StompException;
import org.jboss.stilts.spi.Subscription.AckMode;

public interface SubscriptionBuilder {
    
    SubscriptionBuilder withSelector(String selector);
    SubscriptionBuilder withHeader(String headerName, String headerValue);
    SubscriptionBuilder withMessageHandler(MessageHandler messageHandler);
    SubscriptionBuilder withAckMode(AckMode ackMode);
    
    ClientSubscription start() throws StompException;

}
