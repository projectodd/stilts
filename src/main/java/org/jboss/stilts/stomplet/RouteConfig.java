package org.jboss.stilts.stomplet;

import java.util.Map;

public class RouteConfig {
    
    private String pattern;
    private String className;
    private Map<String, String> properties;

    public RouteConfig() {
        
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return this.pattern;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setProperties(Map<String,String> properties) {
        this.properties = properties;
    }
    
    public Map<String,String> getProperties() {
        return this.properties;
    }

}
