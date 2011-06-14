package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;

public class DefaultSubscriber implements Subscriber {
    
    public DefaultSubscriber(String subscriptionId, String destination, MessageSink messageSink) {
        this.subscriptionId = subscriptionId;
        this.destination = destination;
        this.messageSink = messageSink;
    }
    
    @Override
    public String getId() {
        return this.subscriptionId;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        StompMessage dupe = message.duplicate();
        dupe.getHeaders().put( Header.SUBSCRIPTION, this.subscriptionId );
        this.messageSink.send( dupe );
    }

    @Override
    public String getDestination() {
        return this.destination;
    }
    
    private String subscriptionId;
    private String destination;
    private MessageSink messageSink;

}
