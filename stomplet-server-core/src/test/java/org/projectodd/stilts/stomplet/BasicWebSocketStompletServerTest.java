package org.projectodd.stilts.stomplet;

public class BasicWebSocketStompletServerTest extends BasicStompletServerTest {
    
    public String getConnectionUrl() {
        return "stomp+ws://localhost/";
    }

}
