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

package org.projectodd.stilts.protocol;

import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame.Header;

public class StompFrameEncoder extends OneToOneEncoder {

    private static final int HEADER_ESTIMATE = 1024;

    private static final byte[] HEADER_DELIM = ":".getBytes();
    private static final byte NEWLINE = (byte) '\n';
    private static final byte NULL = (byte) 0x00;

    private Logger log;
    
    public StompFrameEncoder(Logger log) {
        this.log = log;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompFrame) {
            log.trace(  "encode: " + msg  );
            StompFrame frame = (StompFrame) msg;
            ChannelBuffer buffer = newBuffer( frame );
            writeHeader( frame, buffer );
            writeContent( frame, buffer );
            return buffer;
        }
        return null;
    }

    protected ChannelBuffer newBuffer(StompFrame frame) {
        if (frame instanceof StompContentFrame) {
            return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE + ((StompContentFrame) frame).getContent().capacity() );
        }

        return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE );
    }

    protected void writeHeader(StompFrame frame, ChannelBuffer buffer) {
        buffer.writeBytes( frame.getCommand().getBytes() );
        buffer.writeByte( NEWLINE );
        Set<String> headerNames = frame.getHeaderNames();
        for (String name : headerNames) {
            if (name.equalsIgnoreCase( "content-length" )) {
                continue;
            }
            System.err.println( name );
            buffer.writeBytes( name.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( frame.getHeader( name ).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        if (frame instanceof StompContentFrame) {
            int length = ((StompContentFrame) frame).getContent().readableBytes();
            buffer.writeBytes( Header.CONTENT_LENGTH.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( ("" + length).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        buffer.writeByte( NEWLINE );
    }

    protected void writeContent(StompFrame frame, ChannelBuffer buffer) {
        if (frame instanceof StompContentFrame) {
            ChannelBuffer content = ((StompContentFrame)frame).getContent();
            buffer.writeBytes( content );
        }
        buffer.writeByte( NULL );
        return;
    }

}
