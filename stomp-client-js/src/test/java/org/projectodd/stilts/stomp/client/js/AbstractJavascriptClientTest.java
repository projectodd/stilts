package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
    public void setUpRhino() throws Exception {
        this.context = Context.enter();
        this.scope = this.context.initStandardObjects();
        // prepare websocket support;
        evaluate( "var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket" );
        evaluateResource( "test_helper.js" );
    }

    @After
    public void tearDownRhino() {
        Context.exit();
    }

    public Object evaluate(String script) {
        return this.context.evaluateString( this.scope, script, "<cmd>", 1, null );
    }

    public Object evaluateResource(String name) throws IOException {
        InputStream in = getClass().getResourceAsStream( name );
        Reader reader = new InputStreamReader( in );
        try {
            return this.context.evaluateReader( this.scope, reader, name, 1, null );
        } finally {
            reader.close();
        }
    }

}
