package org.projectodd.stilts.stomplet.container;

import org.projectodd.stilts.stomp.StompException;

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
