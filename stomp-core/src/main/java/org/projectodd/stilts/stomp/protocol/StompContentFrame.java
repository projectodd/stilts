/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
