package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.base.AbstractClientAgent;

public class StompletClientAgent extends AbstractClientAgent {

    public StompletClientAgent(StompletServer server, MessageSink messageSink, String sessionId) throws StompException {
        super( server, messageSink, sessionId );
    }

    protected StompletTransaction createTransaction(String transactionId) throws Exception {
        return new StompletTransaction( this, transactionId );
    }

    public StompletServer getServer() {
        return (StompletServer) super.getServer();
    }

}
