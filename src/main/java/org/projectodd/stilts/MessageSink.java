package org.projectodd.stilts;


public interface MessageSink {
    
    void send(StompMessage message) throws StompException;

}
