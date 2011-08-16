package org.projectodd.stilts.conduit.spi;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.spi.StompSession;

public interface StompSessionManager {
    
    StompSession findSession(String sessionId) throws StompException;
    StompSession createSession() throws StompException;
    
}
