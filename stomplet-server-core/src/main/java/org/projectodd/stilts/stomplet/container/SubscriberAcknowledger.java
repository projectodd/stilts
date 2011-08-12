package org.projectodd.stilts.stomplet.container;

import org.projectodd.stilts.stomp.Acknowledger;

class SubscriberAcknowledger implements Acknowledger {

    private SubscriberImpl subscriber;
    private String messageId;

    SubscriberAcknowledger(SubscriberImpl subscriber, String messageId) {
        this.subscriber = subscriber;
        this.messageId = messageId;
    }
    
    @Override
    public void ack() throws Exception {
        this.subscriber.ack( this.messageId );
    }

    @Override
    public void nack() throws Exception {
        this.subscriber.nack( this.messageId );
    }

}
