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
