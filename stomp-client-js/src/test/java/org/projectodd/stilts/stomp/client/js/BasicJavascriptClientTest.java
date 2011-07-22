package org.projectodd.stilts.stomp.client.js;

import org.junit.runner.RunWith;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

@RunWith(JavascriptTestRunner.class)
public class BasicJavascriptClientTest extends AbstractJavascriptClientTest<MockStompProvider> {
    
    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

    
}
