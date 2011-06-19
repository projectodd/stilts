package org.projectodd.stilts.stomplet.helpers;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.StompletConfig;

public abstract class AbstractStomplet implements Stomplet {
    
    @Override
    public void initialize(StompletConfig config) throws StompException {
        this.config = config;
        initialize();
    }
    
    public void initialize() throws StompException {
        // override me in your subclass.
    }
    
    @Override
    public void destroy() throws StompException {
    }
    
    public StompletConfig getStompletConfig() {
        return this.config;
    }
    
    private StompletConfig config;
}
