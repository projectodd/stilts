package org.jboss.stilts.client;

import java.util.concurrent.ExecutionException;

import org.jboss.stilts.StompException;
import org.jboss.stilts.base.DefaultHeaders;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Headers;

public class DefaultSubscriptionBuilder implements SubscriptionBuilder {
    
    private DefaultClientTransaction transaction;
    private Headers headers;
    private MessageHandler messageHandler;

    public DefaultSubscriptionBuilder(DefaultClientTransaction transaction, String destination) {
        this.transaction = transaction;
        this.headers = new DefaultHeaders();
        this.headers.put( Header.DESTINATION, destination );
    }
    
    DefaultClientTransaction getClientTransaction() {
        return this.transaction;
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
    public ClientSubscription start() throws StompException {
        try {
            return this.transaction.subscribe( this );
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }

    public Headers getHeaders() {
        return this.headers;
    }

}
