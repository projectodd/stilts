package org.projectodd.stilts.stomp.spi;

import java.util.Set;

public interface StompSession {
    
    String getId();
    
    Set<String> getAttributeNames();
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    void removeAttribute(String name);
}
