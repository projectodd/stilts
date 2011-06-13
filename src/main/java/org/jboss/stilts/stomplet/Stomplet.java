package org.jboss.stilts.stomplet;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;

public interface Stomplet {
    
    void initialize(StompletConfig config) throws StompException;
    
    void onMessage(MessageRouter router, StompMessage message) throws StompException;
    
    void onSubscribe(MessageSink consumer) throws StompException;
    void onUnsubscribe(MessageSink consumer) throws StompException;

}
