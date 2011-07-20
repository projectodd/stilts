package org.projectodd.stilts.stomp.server;


public class BasicWebSocketsStompServerTest extends BasicStompServerTest {

    public String getConnectionUrl() {
        return "stomp+ws://localhost/";
    }
}