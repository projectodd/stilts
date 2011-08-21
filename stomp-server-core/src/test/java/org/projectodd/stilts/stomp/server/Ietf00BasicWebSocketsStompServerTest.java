package org.projectodd.stilts.stomp.server;

import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf00.Ietf00Handshake;



public class Ietf00BasicWebSocketsStompServerTest extends AbstractWebSocketsStompServerTestCase {

    @Override
    public Class<? extends Handshake> getWebSocketHandshakeClass() {
        return Ietf00Handshake.class;
    }
    

}