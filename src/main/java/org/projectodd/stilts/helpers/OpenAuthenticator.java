package org.projectodd.stilts.helpers;

import org.projectodd.stilts.spi.Authenticator;
import org.projectodd.stilts.spi.Headers;

public class OpenAuthenticator implements Authenticator {

    public static final OpenAuthenticator INSTANCE = new OpenAuthenticator();
    
    private OpenAuthenticator() {
        
    }
    
    @Override
    public boolean authenticate(Headers headers) {
        return true;
    }

}
