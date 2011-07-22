package org.projectodd.stilts.stomp.client.js;

import org.junit.runner.RunWith;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

@RunWith(JavascriptTestRunner.class)
public abstract class AbstractJavascriptClientTest extends AbstractStompServerTestCase<MockStompProvider> {
    
    @Expose
    public StompServer<MockStompProvider> getServer() {
        return this.server;
    }
    
    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

}
