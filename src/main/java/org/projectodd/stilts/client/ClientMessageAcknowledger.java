package org.projectodd.stilts.client;

import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.Headers;

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
