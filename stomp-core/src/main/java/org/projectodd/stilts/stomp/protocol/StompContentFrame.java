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

package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.projectodd.stilts.stomp.spi.Headers;

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
