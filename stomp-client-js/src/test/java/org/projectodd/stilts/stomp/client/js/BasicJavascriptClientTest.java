package org.projectodd.stilts.stomp.client.js;

import org.junit.Test;

public class BasicJavascriptClientTest extends AbstractJavascriptClientTest {
    
    @Test
    public void testClient() throws Exception {
        evaluate("var window = {};" );
        evaluateResource( "/stilts-stomp.js" );
        evaluateResource( "basic_javascript_client_test.js" );
        
        //Thread.sleep( 5000 );
    }

}
