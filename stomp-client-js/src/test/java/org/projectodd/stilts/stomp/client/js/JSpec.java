package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class JSpec {

    private Context context;
    private ScriptableObject scope;
    private ScriptableObject window;

    public JSpec(Context context, ScriptableObject scope, ScriptableObject window) {
        this.context = context;
        this.scope = scope;
        this.window = window;
    }

    
    public Object load(String scriptPath) throws IOException {
        InputStream in = getClass().getResourceAsStream( scriptPath );
        Reader reader = new InputStreamReader( in );
        try {
            return this.context.evaluateReader( this.window, reader, scriptPath, 1, null );
        } finally {
            reader.close();
        }
    }
}
