package org.projectodd.stilts.stomp.client.js;

import org.junit.Test;

public class BasicJavascriptClientTest extends AbstractJavascriptClientTest {
    
    @Test
    public void testClient() throws Exception {
        evaluate( "var ws = new WebSocket('ws://localhost:8675/');" );
        System.err.println( evaluate( "ws.readyState;") );
        System.err.println( evaluate( "ws.send('howdy');") );
        System.err.println( evaluate( "ws.onmessage = function(msg){ java.lang.System.err.println(msg);};" ) );
    }

}
