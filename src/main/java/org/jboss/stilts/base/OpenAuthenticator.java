package org.jboss.stilts.base;

import org.jboss.stilts.spi.Authenticator;
import org.jboss.stilts.spi.Headers;

public class OpenAuthenticator implements Authenticator {

    public static final OpenAuthenticator INSTANCE = new OpenAuthenticator();
    
    private OpenAuthenticator() {
        
    }
    
    @Override
    public boolean authenticate(Headers headers) {
        return true;
    }

}
