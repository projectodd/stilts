package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface Stomplet {
    
    void initialize(StompletConfig config) throws StompException;
    void destroy() throws StompException;
    
    void onMessage(StompMessage message) throws StompException;
    
    void onSubscribe(Subscriber subscriber) throws StompException;
    void onUnsubscribe(Subscriber subscriber) throws StompException;

}
