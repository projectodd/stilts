package org.projectodd.stilts.stomp.client.js;

import static org.junit.Assert.*;

import org.junit.Test;

public class BasicJavascriptClientTest extends AbstractJavascriptClientTest {
    
    public void initJavascript() {
    }
    
    @Test
    public void testClient() throws Exception {
        evaluateResource( "/stilts-stomp.js" );
        Object result = evaluateResource( "basic_javascript_client_test.js" );
        
        assertNotNull( result );
        assertEquals( "completed", result.toString() );
    }

}
