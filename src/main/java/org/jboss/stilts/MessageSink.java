package org.jboss.stilts;


public interface MessageSink {
    
    void send(StompMessage message) throws StompException;

}
