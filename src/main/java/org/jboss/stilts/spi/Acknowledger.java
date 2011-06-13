package org.jboss.stilts.spi;

public interface Acknowledger {
    
    String getId();
    void acknowledge() throws Exception;

}
