package org.projectodd.stilts.stomp.server.protocol.http;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.stomp.server.protocol.ConnectionContext;

public class ConnectionManager {
    
    private Map<String, ConnectionContext> connections = new HashMap<String, ConnectionContext>();
    
    public ConnectionManager() {
        
    }
    
    public ConnectionContext get(String connectionId) {
        return this.connections.get(  connectionId  );
    }
    
    public void put(String connectionId, ConnectionContext connectionContext) {
        this.connections.put( connectionId, connectionContext );
    }

}
