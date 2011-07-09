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

import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class StompFrameEncoder extends OneToOneEncoder {

    private static final int HEADER_ESTIMATE = 1024;

    private static final byte[] HEADER_DELIM = ":".getBytes();
    private static final byte NEWLINE = (byte) '\n';
    private static final byte NULL = (byte) 0x00;

    private static Logger log = Logger.getLogger(StompFrameEncoder.class);

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
