package org.jboss.stilts;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.spi.Headers;

public interface StompMessage {
    
    String getId();
    Headers getHeaders();
    
    String getDestination();
    void setDestination(String destination);
    
    String getContentType();
    void setContentType(String contentType);
    
    String getContentAsString();
    void setContentAsString(String content);
    
    ChannelBuffer getContent();
    void setContent(ChannelBuffer content);
    
    boolean isError();
    void acknowledge() throws StompException;
    
    StompMessage duplicate();
    
}
