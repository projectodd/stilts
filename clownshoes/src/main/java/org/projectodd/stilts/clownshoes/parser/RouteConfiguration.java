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

package org.projectodd.stilts.clownshoes.parser;

import java.util.HashMap;
import java.util.Map;

public class RouteConfiguration {
    
    public RouteConfiguration(String pattern, String className) {
       this.pattern = pattern;
       this.className = className;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public String getStompletClassName() {
        return this.className;
    }
    
    public Map<String,String> getProperties() {
        return this.properties;
    }
    
    public String toString() {
        return "[RouteConfiguration: pattern=" + this.pattern + "\n" 
            + "className=" + this.className + "\n"
            + "properties=" + this.properties + "]";
        
    }
    
    private final String pattern;
    private final String className;
    private final Map<String, String> properties = new HashMap<String,String>();


}
