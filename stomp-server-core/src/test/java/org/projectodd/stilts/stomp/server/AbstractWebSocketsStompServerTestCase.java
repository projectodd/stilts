package org.projectodd.stilts.stomp.server;

import java.net.URISyntaxException;

import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;


public abstract class AbstractWebSocketsStompServerTestCase extends BasicStompServerTest {

    public String getConnectionUrl() {
        return "stomp+ws://localhost/";
    }
    
    public StompClient createClient() throws URISyntaxException {
        StompClient client = new StompClient( getConnectionUrl() );
        client.setWebSocketHandshakeClass( getWebSocketHandshakeClass() );
        return client;
    }
    
    public abstract Class<? extends Handshake> getWebSocketHandshakeClass();
}