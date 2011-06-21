/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.stomp.spi.Headers;

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
