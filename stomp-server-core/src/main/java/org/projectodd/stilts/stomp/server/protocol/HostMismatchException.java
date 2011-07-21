package org.projectodd.stilts.stomp.server.protocol;

import org.projectodd.stilts.stomp.StompException;

public class HostMismatchException extends StompException {

    private static final long serialVersionUID = 1L;
    
    public HostMismatchException(String transportHost, String stompHost) {
        super( "Host provided by transport: '" + transportHost + "' does not match host provided by STOMP connection: '" + stompHost + "'" );
        this.transportHost = transportHost;
        this.stompHost = stompHost;
    }
    
    public String getTransportHost() {
        return this.transportHost;
    }
    
    public String getStompHost() {
        return this.stompHost;
    }

    private String transportHost;
    private String stompHost;
}
