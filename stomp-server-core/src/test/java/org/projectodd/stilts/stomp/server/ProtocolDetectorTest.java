package org.projectodd.stilts.stomp.server;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.server.protocol.ProtocolDetector;

public class ProtocolDetectorTest {
    
    private MockStompProvider mockProvider;
    private ProtocolDetector detector;
    private DecoderEmbedder<ChannelBuffer> decoder;

    @Before
    public void setUp() {
        this.mockProvider = new MockStompProvider();
        this.detector = new ProtocolDetector( mockProvider, null );
        this.decoder = new DecoderEmbedder<ChannelBuffer>( this.detector );
    }
    
    @Test
    public void testBogusDetection() throws Exception {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes( "CONNECT".getBytes() );
        buffer.writeByte(  '\n'  );
        System.err.println( "Offer: " + buffer.toString() );
        boolean result = this.decoder.offer( buffer );
        
        ChannelBuffer nextMessage = this.decoder.peek();
        
        System.err.println( "[" + nextMessage.toString( Charset.forName( "UTF-8" )) + "]" );
        
        System.err.println( result );
    }
    
}
