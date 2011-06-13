package org.jboss.stilts.jms;

import javax.jms.Message;

import org.jboss.stilts.spi.Acknowledger;

public class JMSAcknowledger implements Acknowledger {

    public JMSAcknowledger(String id, Message message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void acknowledge() throws Exception {
        this.message.acknowledge();
    }

    private final String id;
    private final Message message;

}
