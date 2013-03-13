package org.projectodd.stilts.stomp.server.protocol.http;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.stomp.server.protocol.ConnectionContext;

public class SinkManager {

    public SinkManager() {

    }

    public HttpMessageSink get(ConnectionContext connection) {
        return this.sinks.get( connection );
    }

    public void put(ConnectionContext connection, HttpMessageSink sink) {
        this.sinks.put( connection, sink );
    }

    private Map<ConnectionContext, HttpMessageSink> sinks = new HashMap<ConnectionContext, HttpMessageSink>();

}
