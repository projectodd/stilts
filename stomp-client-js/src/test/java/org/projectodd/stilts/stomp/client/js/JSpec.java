package org.projectodd.stilts.stomp.client.js;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class JSpec {
    
    private static JSpec current = null;
    
    public static void setCurrent(JSpec current) {
        JSpec.current = current;
    }
    
    public static JSpec getCurrent() {
        return JSpec.current;
    }
    

    public JSpec(Context context, ScriptableObject scope, ScriptableObject window) {
        this.context = context;
        this.scope = scope;
        this.window = window;
    }
    
    public void reset() {
        this.errors.clear();
    }
    
    public void addError(Throwable t) {
        this.errors.add(  t );
    }
    
    public List<Throwable> getErrors() {
        return this.errors;
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
    
    private Context context;
    private ScriptableObject scope;
    private ScriptableObject window;
    
    private final List<Throwable> errors = new ArrayList<Throwable>();
}
