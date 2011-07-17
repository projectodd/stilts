package org.projectodd.stilts.stomp.server;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomp.spi.TransactionalAcknowledgeableMessageSink;

public class MockStompProvider implements StompProvider {

    public MockStompConnection createConnection(TransactionalAcknowledgeableMessageSink messageSink, Headers headers, Version version) throws StompException {
        return createConnection(messageSink, headers, version, null);
    }    
    
    @Override
    public MockStompConnection createConnection(TransactionalAcknowledgeableMessageSink messageSink, Headers headers, Version version, Heartbeat hb) throws StompException {
        MockStompConnection connection = new MockStompConnection( "session-" + (++this.sessionCounter), version, hb );
        this.connections.add( connection );
        return connection;
    }
    
    public List<MockStompConnection> getConnections() {
        return this.connections;
    }
    
    private List<MockStompConnection> connections = new ArrayList<MockStompConnection>();
    private int sessionCounter = 0;

}
