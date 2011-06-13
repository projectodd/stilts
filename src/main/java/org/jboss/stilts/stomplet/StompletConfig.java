package org.jboss.stilts.stomplet;

public interface StompletConfig {
    
    String getProperty(String name);
    String[] getPropertyNames();
    
    Object getAttribute(String name);
    String[] getAttributeNames();
    

}
