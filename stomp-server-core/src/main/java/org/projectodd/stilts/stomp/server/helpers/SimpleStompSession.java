package org.projectodd.stilts.stomp.server.helpers;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.projectodd.stilts.stomp.spi.StompSession;

public class SimpleStompSession implements StompSession {

    public SimpleStompSession(String id) {
        this.id = id;
    }
    
    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Set<String> getAttributeNames() {
        return this.attributes.keySet();
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get( name );
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.put( name, value );
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove( name );
    }
    
    private String id; 
    private Map<String, Object> attributes = new HashMap<String, Object>();

}
