/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp;

import org.jboss.netty.buffer.ChannelBuffer;

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
    
    void ack(String transactionId) throws StompException;
    void nack(String transactionId) throws StompException;
    
    StompMessage duplicate();
    
}
