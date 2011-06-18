package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;

public interface Subscription {
    
    public static enum AckMode {
        AUTO,
        CLIENT,
        CLIENT_INDIVIDUAL,
    }
    
    String getId();
    void cancel() throws StompException;
}
