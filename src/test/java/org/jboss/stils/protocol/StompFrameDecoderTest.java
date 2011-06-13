package org.jboss.stils.protocol;

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
import org.jboss.stilts.protocol.StompContentFrame;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.transport.StompFrameDecoder;
import org.junit.Before;
import org.junit.Test;

public class StompFrameDecoderTest {

    private DecoderEmbedder<StompFrame> decoder;

    @Before
    public void setUp() {
        this.decoder = new DecoderEmbedder<StompFrame>( new StompFrameDecoder() );
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
