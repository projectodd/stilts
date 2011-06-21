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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.Map;

import org.projectodd.stilts.stomplet.StompletConfig;
import org.projectodd.stilts.stomplet.StompletContext;

public class DefaultStompletConfig implements StompletConfig {

    public DefaultStompletConfig(StompletContext stompletContext, Map<String,String> properties) {
        this.stompletContext = stompletContext;
        this.properties = properties;
    }
    
    @Override
    public StompletContext getStompletContext() {
        return this.stompletContext;
    }

    @Override
    public String getProperty(String name) {
        return this.properties.get(  name  );
    }

    @Override
    public String[] getPropertyNames() {
        return this.properties.keySet().toArray( new String[this.properties.size()] );
    }

    private final StompletContext stompletContext;
    private final Map<String,String> properties;
    

}
