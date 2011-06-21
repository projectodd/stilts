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

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.helpers.DefaultHeaders;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.Subscription.AckMode;

public class DefaultSubscriptionBuilder implements SubscriptionBuilder {
    
    public DefaultSubscriptionBuilder(AbstractStompClient client, String destination) {
        this.client = client;
        this.headers = new DefaultHeaders();
        this.headers.put( Header.DESTINATION, destination );
    }
    
    @Override
    public SubscriptionBuilder withMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }
    
    public MessageHandler getMessageHandler() {
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
    
    @Override
    public SubscriptionBuilder withAckMode(AckMode ackMode) {
        this.headers.put( Header.ACK, ackMode.toString() );
        return this;
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

    public Headers getHeaders() {
        return this.headers;
    }

    private AbstractStompClient client;
    private Headers headers;
    private MessageHandler messageHandler;

}
