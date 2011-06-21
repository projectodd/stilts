/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.client;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.spi.Subscription.AckMode;

public interface SubscriptionBuilder {
    
    SubscriptionBuilder withSelector(String selector);
    SubscriptionBuilder withHeader(String headerName, String headerValue);
    SubscriptionBuilder withMessageHandler(MessageHandler messageHandler);
    SubscriptionBuilder withAckMode(AckMode ackMode);
    
    ClientSubscription start() throws StompException;

}
