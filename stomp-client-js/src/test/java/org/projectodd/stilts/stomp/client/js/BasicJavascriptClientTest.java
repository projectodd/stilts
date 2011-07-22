package org.projectodd.stilts.stomp.client.js;

import org.junit.Test;

public class BasicJavascriptClientTest extends AbstractJavascriptClientTest {
    
    public void initJavascript() {
    }
    
    @Test
    public void testClient() throws Exception {
        evaluateResource( "/stilts-stomp.js" );
        evaluateResource( "basic_javascript_client_test.js" );
    }

}
