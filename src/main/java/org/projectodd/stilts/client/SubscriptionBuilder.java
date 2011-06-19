package org.projectodd.stilts.client;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.spi.Subscription.AckMode;

public interface SubscriptionBuilder {
    
    SubscriptionBuilder withSelector(String selector);
    SubscriptionBuilder withHeader(String headerName, String headerValue);
    SubscriptionBuilder withMessageHandler(MessageHandler messageHandler);
    SubscriptionBuilder withAckMode(AckMode ackMode);
    
    ClientSubscription start() throws StompException;

}
