package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.After;
import org.junit.Before;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;
import org.projectodd.stilts.stomp.spi.StompProvider;

public abstract class AbstractJavascriptClientTest<T extends StompProvider> {
    
    @Expose
    protected StompServer<T> server;

    @Before
    public void setUpServer() throws Exception {
        this.server = createServer();
        this.server.start();
    }
    
    protected abstract StompServer<T> createServer() throws Exception;
    
    @After
    public void tearDownServer() throws Exception {
        this.server.stop();
        this.server = null;
        
    }
    
    public StompServer<T> getServer() {
        return this.server;
    }


    protected Context context;
    protected ScriptableObject scope;
    private ScriptableObject window;

    @Before
    public void setUpRhino() throws Exception {
        this.context = Context.enter();
        this.scope = this.context.initStandardObjects();
        // prepare websocket support;
        this.window = (ScriptableObject) this.context.evaluateString( this.scope, "var window = {}; window;", "<cmd>", 1, null );
        this.window.put( "server", this.window, this.server );
        evaluateResource( "test_helper.js" );
        initJavascript();
    }
    
    public void initJavascript() {
    }

    @After
    public void tearDownRhino() {
        Context.exit();
    }

    public Object evaluate(String script) {
        return this.context.evaluateString( this.window, script, "<cmd>", 1, null );
    }

    public Object evaluateResource(String name) throws IOException {
        InputStream in = getClass().getResourceAsStream( name );
        Reader reader = new InputStreamReader( in );
        try {
            return this.context.evaluateReader( this.window, reader, name, 1, null );
        } finally {
            reader.close();
        }
    }

}
