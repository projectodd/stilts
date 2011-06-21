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
import org.projectodd.stilts.StompMessage;

public class DefaultClientSubscription implements ClientSubscription {
    
    public DefaultClientSubscription(AbstractStompClient client, String id, MessageHandler messageHandler) {
        this.client = client;
        this.id = id;
        this.messageHandler = messageHandler;
        this.active = true;
    }
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    public MessageHandler getMessageHandler() {
        return this.messageHandler;
    }
    
    void messageReceived(StompMessage message) {
        if ( this.messageHandler != null ) {
            this.messageHandler.handle( message );
        }
        
    }

    @Override
    public void unsubscribe() throws StompException {
        try {
            this.client.unsubscribe( this );
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }
    
    private AbstractStompClient client;
    private MessageHandler messageHandler;
    private String id;
    private boolean active;

}
