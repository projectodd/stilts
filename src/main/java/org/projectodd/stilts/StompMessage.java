package org.projectodd.stilts;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.spi.Headers;

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
    void ack() throws StompException;
    void nack() throws StompException;
    
    StompMessage duplicate();
    
}
