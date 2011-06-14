package org.jboss.stilts.stomplet;

import java.util.Map;

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
