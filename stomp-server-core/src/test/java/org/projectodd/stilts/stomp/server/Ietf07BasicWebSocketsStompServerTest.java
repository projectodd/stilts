package org.projectodd.stilts.stomp.server;

import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf07.Ietf07Handshake;

public class Ietf07BasicWebSocketsStompServerTest extends AbstractWebSocketsStompServerTestCase {

    @Override
    public Class<? extends Handshake> getWebSocketHandshakeClass() {
        return Ietf07Handshake.class;
    }
    

}