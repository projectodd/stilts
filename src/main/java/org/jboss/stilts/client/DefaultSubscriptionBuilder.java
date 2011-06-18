package org.jboss.stilts.client;

import java.util.concurrent.ExecutionException;

import org.jboss.stilts.StompException;
import org.jboss.stilts.base.DefaultHeaders;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Headers;

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
