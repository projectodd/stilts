package org.projectodd.stilts.stomplet.container;

import org.projectodd.stilts.stomp.Acknowledger;

public class NoOpAcknowledger implements Acknowledger {

    @Override
    public void ack() throws Exception {
    }

    @Override
    public void nack() throws Exception {
    }

}
