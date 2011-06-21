/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
