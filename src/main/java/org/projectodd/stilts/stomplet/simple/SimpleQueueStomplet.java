package org.projectodd.stilts.stomplet.simple;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class SimpleQueueStomplet extends SimpleSubscribableStomplet implements AcknowledgeableStomplet  {

    @Override
    public void onMessage(StompMessage message) throws StompException {
        sendToOneSubscriber( message );
    }

    @Override
    public void ack(Subscriber subscriber, StompMessage message) {
        System.err.println( "ACK ACK ACK: " + message );
        // yay
    }

    @Override
    public void nack(Subscriber subscriber, StompMessage message) {
        System.err.println( "NACK NACK NACK: " + message );
        try {
            sendToOneSubscriber( message );
        } catch (StompException e) {
            e.printStackTrace();
        }
    }

}