package org.jboss.stilts.client;

import java.util.concurrent.ExecutionException;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public class DefaultClientSubscription implements ClientSubscription {
    
    public DefaultClientSubscription(DefaultClientTransaction transaction, String id, MessageHandler messageHandler) {
        this.transaction = transaction;
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
    public DefaultClientTransaction getTransaction() {
        return this.transaction;
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
            this.transaction.unsubscribe( this );
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }
    
    private DefaultClientTransaction transaction;
    private MessageHandler messageHandler;
    private String id;
    private boolean active;



}
