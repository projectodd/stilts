package org.jboss.stilts;

import org.jboss.stilts.spi.Headers;

public interface StompMessage {
    
    Headers getHeaders();
    
    String getDestination();
    void setDestination(String destination);
    
    String getContentType();
    void setContentType(String contentType);
    
    String getContent();
    void setContent(String content);
    
    boolean isError();
    
    StompMessage duplicate();
    
}
