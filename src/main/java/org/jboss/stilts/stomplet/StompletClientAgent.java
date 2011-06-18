package org.jboss.stilts.stomplet;

import javax.transaction.TransactionManager;

import org.jboss.stilts.StompException;
import org.jboss.stilts.base.AbstractClientAgent;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.spi.Subscription;
import org.jboss.stilts.spi.Subscription.AckMode;

public class StompletClientAgent extends AbstractClientAgent {

    public StompletClientAgent(StompletContainer stompletContainer, TransactionManager transactionManager, StompProvider provider, AcknowledgeableMessageSink messageSink, String sessionId) throws StompException {
        super( transactionManager, provider, messageSink, sessionId );
        this.stompletContainer = stompletContainer;
    }
    

    @Override
    public Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws Exception {
        RouteMatch match = this.stompletContainer.match( destination );
        if (match == null) {
            return null;
        }
        
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
        
        Subscriber subscriber = new DefaultSubscriber( stomplet, subscriptionId, destination, this.getMessageSink(), ackMode );
        stomplet.onSubscribe( subscriber );
        return new StompletSubscription( stomplet, subscriber );
    }
    
    private StompletContainer stompletContainer;

}
