package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.StompException;

public class NoSuchHostException extends StompException {
    
    private static final long serialVersionUID = 1L;
    
    private String host;

    public NoSuchHostException(String host) {
        super( "No such host: " + host );
        this.host = host;
    }
    
    public String getHost() {
        return this.host;
    }

}
