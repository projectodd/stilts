package org.jboss.stilts.client;

import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.Headers;

public class ClientMessageAcknowledger implements Acknowledger {

    public ClientMessageAcknowledger(AbstractStompClient client, Headers headers) {
        this.client = client;
        this.headers = headers;
    }

    @Override
    public void ack() throws Exception {
        client.sendFrame( StompFrames.newAckFrame( this.headers ) );
    }

    @Override
    public void nack() throws Exception {
        client.sendFrame( StompFrames.newNackFrame( this.headers ) );
    }

    private AbstractStompClient client;
    private Headers headers;


}
