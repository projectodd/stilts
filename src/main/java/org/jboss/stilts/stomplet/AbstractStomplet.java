package org.jboss.stilts.stomplet;

import org.jboss.stilts.StompException;

public abstract class AbstractStomplet implements Stomplet {
    
    @Override
    public void initialize(StompletConfig config) throws StompException {
        this.config = config;
        initialize();
    }
    
    public void initialize() throws StompException {
        // override me in your subclass.
    }
    
    public StompletConfig getStompletConfig() {
        return this.config;
    }
    
    private StompletConfig config;
}
