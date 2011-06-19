package org.jboss.stilts.circus.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.circus.MessageConduit;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.Subscription;
import org.jboss.stilts.spi.Subscription.AckMode;
import org.jboss.stilts.stomplet.Stomplet;
import org.jboss.stilts.stomplet.Subscriber;

public class StompletMessageConduit implements MessageConduit {

    public StompletMessageConduit(StompletContainer stompletContainer, AcknowledgeableMessageSink messageSink) throws StompException {
        this.stompletContainer = stompletContainer;
        this.messageSink = messageSink;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.stompletContainer.send( message );
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        RouteMatch match = this.stompletContainer.match( destination );
        System.err.println( "SUBSCRIBER MATCH: " + match );
        if (match == null) {
            return null;
        }
        
        System.err.println( "ADD SUBSCRIBER: " + match );
        
        Stomplet stomplet = match.getRoute().getStomplet();
        
        String ackHeader = headers.get( Header.ACK );
        
        AckMode ackMode = AckMode.AUTO;
        
        if ( ackHeader == null || "auto".equalsIgnoreCase( ackHeader ) ) {
            ackMode = AckMode.AUTO;
        } else if ( "client".equalsIgnoreCase( ackHeader ) ){
            ackMode = AckMode.CLIENT;
        } else if ( "client-individual".equalsIgnoreCase( ackHeader ) ){
            ackMode = AckMode.CLIENT_INDIVIDUAL;
        }
        
        Subscriber subscriber = new DefaultSubscriber( stomplet, subscriptionId, destination, this.messageSink, ackMode );
        stomplet.onSubscribe( subscriber );
        return new StompletSubscription( stomplet, subscriber );
    }
    
    private StompletContainer stompletContainer;
    private AcknowledgeableMessageSink messageSink;


}
