package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.Subscription.AckMode;

public class DefaultSubscriber implements Subscriber {

    
    public DefaultSubscriber(Stomplet stomplet, String subscriptionId, String destination, AcknowledgeableMessageSink messageSink, AckMode ackMode) {
        this.stomplet = stomplet;
        this.subscriptionId = subscriptionId;
        this.destination = destination;
        this.messageSink = messageSink;
        this.ackMode = ackMode;
        
        if ( this.ackMode == AckMode.CLIENT ) {
            this.ackSet = new CummulativeAckSet();
        } else if ( this.ackMode == AckMode.CLIENT_INDIVIDUAL ) {
            this.ackSet = new IndividualAckSet();
        }
    }

    @Override
    public String getId() {
        return this.subscriptionId;
    }

    public AckMode getAckMode() {
        return this.ackMode;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        StompletAcknowledger acknowledger = null;

        if (this.stomplet instanceof AcknowledgeableStomplet) {
            acknowledger = new StompletAcknowledger( (AcknowledgeableStomplet) this.stomplet, this, message );
        } 

        send( message, acknowledger );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        StompMessage dupe = message.duplicate();
        dupe.getHeaders().put( Header.SUBSCRIPTION, this.subscriptionId );
        if (this.ackMode == AckMode.AUTO && acknowledger != null ) {
            try {
                acknowledger.ack();
            } catch (Exception e) {
                throw new StompException( e );
            }
            this.messageSink.send( dupe );
        } else {
            this.messageSink.send( dupe, acknowledger );
        }
    }

    @Override
    public String getDestination() {
        return this.destination;
    }

    private Stomplet stomplet;
    private AcknowledgeableMessageSink messageSink;
    private String subscriptionId;
    private String destination;
    private AckMode ackMode;
    private AckSet ackSet;

}
