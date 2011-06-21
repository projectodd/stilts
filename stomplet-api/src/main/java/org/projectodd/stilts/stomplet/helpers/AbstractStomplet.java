/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

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
