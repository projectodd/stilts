package org.projectodd.stilts.conduit.stomp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.projectodd.stilts.conduit.spi.StompSessionManager;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.server.helpers.SimpleStompSession;
import org.projectodd.stilts.stomp.spi.StompSession;

public class SimpleStompSessionManager implements StompSessionManager {

    @Override
    public StompSession findSession(String sessionId) throws StompException {
        return null;
    }

    @Override
    public StompSession createSession() throws StompException {
        SimpleStompSession session = new SimpleStompSession( getNextSessionId() );
        this.sessions.put( session.getId(), session );
        return session;
    }
    
    protected String getNextSessionId() {
        return "session-" + this.counter.getAndIncrement();
    }
    
    private AtomicLong counter = new AtomicLong();
    private Map<String, SimpleStompSession> sessions = new HashMap<String,SimpleStompSession>();

}
