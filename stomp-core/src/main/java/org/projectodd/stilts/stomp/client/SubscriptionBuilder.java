/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.client;

import java.util.concurrent.Executor;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.spi.Subscription.AckMode;

public interface SubscriptionBuilder {
    
    SubscriptionBuilder withSelector(String selector);
    SubscriptionBuilder withHeader(String headerName, String headerValue);
    SubscriptionBuilder withMessageHandler(MessageHandler messageHandler);
    SubscriptionBuilder withAckMode(AckMode ackMode);
    SubscriptionBuilder withExecutor(Executor executor);
    
    ClientSubscription start() throws StompException;

}
