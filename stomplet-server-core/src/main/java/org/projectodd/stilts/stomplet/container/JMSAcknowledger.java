package org.projectodd.stilts.stomplet.container;

import javax.jms.Message;

import org.projectodd.stilts.stomp.Acknowledger;

public class JMSAcknowledger implements Acknowledger {

    private Message message;

    public JMSAcknowledger(Message message) {
        this.message = message;
    }
    
    @Override
    public void ack() throws Exception {
        this.message.acknowledge();
    }

    @Override
    public void nack() throws Exception {
        // nothing?
    }

}
