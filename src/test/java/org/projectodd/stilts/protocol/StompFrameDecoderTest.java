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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.logging.SimpleLogger;
import org.projectodd.stilts.protocol.StompContentFrame;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrameDecoder;
import org.projectodd.stilts.protocol.StompFrame.Command;

public class StompFrameDecoderTest {

    private DecoderEmbedder<StompFrame> decoder;

    @Before
    public void setUp() {
        this.decoder = new DecoderEmbedder<StompFrame>( new StompFrameDecoder( SimpleLogger.DEFAULT ) );
    }

    @Test
    public void testDecodeConnect() throws Exception {
        ChannelBuffer bytes = read( "valid-connect.msg", true );
        this.decoder.offer( bytes );
        StompFrame frame = this.decoder.poll();
        assertNotNull( frame );
    }

    @Test
    public void testDecodeIncomplete() throws Exception {
        ChannelBuffer bytes = read( "incomplete-connect.msg", false );
        this.decoder.offer( bytes );
        StompFrame frame = this.decoder.poll();
        assertNull( frame );
        this.decoder.offer( ChannelBuffers.copiedBuffer( new byte[] { '\n', 0x00 } ) );
        frame = this.decoder.poll();
        assertNotNull( frame );
        assertEquals( Command.CONNECT, frame.getCommand() );
    }

    @Test
    public void testDecodeNullTerminatedMessage() throws Exception {
        ChannelBuffer bytes = read( "null-terminated.msg", true );
        this.decoder.offer( bytes );
        StompFrame frame = this.decoder.poll();
        assertNotNull( frame );
        assertTrue( frame instanceof StompContentFrame );
        assertEquals( Command.SEND, frame.getCommand() );
        assertEquals( "This is my message.\n", ((StompContentFrame) frame).getContent().toString( Charset.forName( "UTF-8" ) ) );
    }

    ChannelBuffer read(String name, boolean appendNull) throws IOException {
        InputStream stream = getClass().getResourceAsStream( name );

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

        int c = -1;
        while ((c = stream.read()) >= 0) {
            buffer.writeByte( c );
        }

        if (appendNull) {
            buffer.writeByte( 0x00 );
        }

        return buffer;
    }

}
