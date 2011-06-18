package org.jboss.stilts.jms;

import javax.jms.Message;

import org.jboss.stilts.spi.Acknowledger;

public class JMSMessageAcknowledger implements Acknowledger {

    public JMSMessageAcknowledger(Message message) {
        this.message = message;
    }

    @Override
    public void ack() throws Exception {
        this.message.acknowledge();
    }

    @Override
    public void nack() throws Exception {
        // nothing
    }

    private Message message;

}
