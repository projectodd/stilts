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

package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.projectodd.stilts.stomp.Headers;

public class StompContentFrame extends StompFrame {

    public StompContentFrame(Command command) {
        super( command );
    }
    
    public StompContentFrame(Command command, Headers headers) {
        super( command, headers );
    }
    
    public StompContentFrame(FrameHeader header) {
        super( header );
    }
    
    public StompContentFrame(FrameHeader header, ChannelBuffer content) {
        super( header );
        this.content = content;
    }
    
    public StompContentFrame(FrameHeader header, String content) {
        super( header);
        this.content = ChannelBuffers.copiedBuffer( content.getBytes() );
    }
    
    public void setContent(ChannelBuffer content) {
        this.content = content;
    }

    public ChannelBuffer getContent() {
        return ChannelBuffers.wrappedBuffer( this.content );
    }
    
    private ChannelBuffer content;
}
