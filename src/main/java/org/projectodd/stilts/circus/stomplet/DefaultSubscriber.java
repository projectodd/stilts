package org.projectodd.stilts.circus.stomplet;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.Subscription.AckMode;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class DefaultSubscriber implements Subscriber {

    public DefaultSubscriber(Stomplet stomplet, String subscriptionId, String destination, AcknowledgeableMessageSink messageSink, AckMode ackMode) {
        this.stomplet = stomplet;
        this.subscriptionId = subscriptionId;
        this.destination = destination;
        this.messageSink = messageSink;
        this.ackMode = ackMode;

        if (this.ackMode == AckMode.CLIENT) {
            this.ackSet = new CummulativeAckSet();
        } else if (this.ackMode == AckMode.CLIENT_INDIVIDUAL) {
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

        send( message, null );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        StompMessage dupe = message.duplicate();
        dupe.getHeaders().put( Header.SUBSCRIPTION, this.subscriptionId );
        
        if ((acknowledger == null) && (this.stomplet instanceof AcknowledgeableStomplet)) {
            System.err.println( "creating acknowledger for the stomplet" );
            acknowledger = new StompletAcknowledger( (AcknowledgeableStomplet) this.stomplet, this, dupe );
        }

        if (this.ackMode == AckMode.AUTO && acknowledger != null) {
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
