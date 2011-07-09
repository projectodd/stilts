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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

class SubscriptionBuilderImpl implements SubscriptionBuilder {
    
    SubscriptionBuilderImpl(StompClient client, String destination) {
        this.client = client;
        this.headers = new DefaultHeaders();
        this.headers.put( Header.DESTINATION, destination );
    }
    
    @Override
    public SubscriptionBuilder withMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }
    
    MessageHandler getMessageHandler() {
        return this.messageHandler;
    }

    @Override
    public SubscriptionBuilder withSelector(String selector) {
        this.headers.put(  Header.SELECTOR, selector );
        return this;
    }

    @Override
    public SubscriptionBuilder withHeader(String headerName, String headerValue) {
        this.headers.put( headerName, headerValue );
        return this;
    }
    
    Headers getHeaders() {
        return this.headers;
    }

    @Override
    public SubscriptionBuilder withAckMode(AckMode ackMode) {
        this.headers.put( Header.ACK, ackMode.toString() );
        return this;
    }
    
    @Override
    public SubscriptionBuilder withExecutor(Executor executor) {
        this.executor = executor;
        return this;
    }
    
    Executor getExecutor() {
        return this.executor;
    }

    @Override
    public ClientSubscription start() throws StompException {
        try {
            return this.client.subscribe( this );
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }

    private final StompClient client;
    private Headers headers;
    private MessageHandler messageHandler;
    private Executor executor;

}
