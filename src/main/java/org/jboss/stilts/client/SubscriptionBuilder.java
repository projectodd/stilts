package org.jboss.stilts.client;

import org.jboss.stilts.StompException;

public interface SubscriptionBuilder {
    
    SubscriptionBuilder withSelector(String selector);
    SubscriptionBuilder withHeader(String headerName, String headerValue);
    SubscriptionBuilder withMessageHandler(MessageHandler messageHandler);
    ClientSubscription start() throws StompException;

}
