package org.jboss.stilts.stomplet;

public interface StompletContext {
    
    String[] getAttributeNames();
    Object getAttribute(String name);
    
    MessageRouter getMessageRouter();

}
