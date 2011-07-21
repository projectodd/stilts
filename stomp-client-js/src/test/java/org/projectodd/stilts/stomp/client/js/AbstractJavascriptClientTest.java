package org.projectodd.stilts.stomp.client.js;

import org.junit.After;
import org.junit.Before;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;

public abstract class AbstractJavascriptClientTest extends AbstractStompServerTestCase<MockStompProvider> {

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

    protected Context context;
    protected ScriptableObject scope;

    @Before
    public void setUpRhino() {
        this.context = Context.enter();
        this.scope = this.context.initStandardObjects();
        // prepare websocket support;
        evaluate( "var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket" );
    }

    @After
    public void tearDownRhino() {
        Context.exit();
    }

    public Object evaluate(String script) {
        return this.context.evaluateString( this.scope, script, "<cmd>", 1, null );
    }

}
