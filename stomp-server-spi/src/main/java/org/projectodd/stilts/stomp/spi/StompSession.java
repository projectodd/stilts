package org.projectodd.stilts.stomp.spi;

import java.util.List;

public interface StompSession {
    
    String getId();
    
    List<String> getAttributeNames();
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    void removeAttribute(String name);
}
