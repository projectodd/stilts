package org.projectodd.stilts.stomp.server;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.PipelineExposer;
import org.projectodd.stilts.stomp.server.protocol.ProtocolDetector;

public class ProtocolDetectorTest {

    private MockStompProvider mockProvider;
    private PipelineExposer pipelineExposer;
    private ProtocolDetector detector;
    private DecoderEmbedder<ChannelBuffer> decoder;

    @Before
    public void setUp() {
        this.mockProvider = new MockStompProvider();
        this.pipelineExposer = new PipelineExposer();
        this.detector = new ProtocolDetector( mockProvider, null );
        this.decoder = new DecoderEmbedder<ChannelBuffer>( this.pipelineExposer, this.detector );
    }

    @Test
    public void testSimpleStompDetection() throws Exception {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes( "CONNECT\n".getBytes() );

        boolean result = this.decoder.offer( buffer );
        assertTrue( result );

        ChannelBuffer nextMessage = this.decoder.peek();
        String text = nextMessage.toString( Charset.forName( "UTF-8" ) );
        assertEquals( "CONNECT\n", text );

        List<String> handlerNames = getHandlerNames();

        assertEquals( "stomp-frame-encoder", handlerNames.get( 1 ) );
        assertEquals( "stomp-frame-decoder", handlerNames.get( 2 ) );
    }

    @Test
    public void testWebSocketDetection() throws Exception {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes( "GET / HTTP/1.1\n".getBytes() );

        boolean result = this.decoder.offer( buffer );
        assertTrue( result );

        ChannelBuffer nextMessage = this.decoder.peek();
        String text = nextMessage.toString( Charset.forName( "UTF-8" ) );
        assertEquals( "GET / HTTP/1.1\n", text );

        List<String> handlerNames = getHandlerNames();

        assertEquals( "http-encoder", handlerNames.get( 1 ) );
        assertEquals( "http-decoder", handlerNames.get( 2 ) );
        assertEquals( "websocket-handshake", handlerNames.get( 3 ) );
        assertEquals( "stomp-frame-encoder", handlerNames.get( 4 ) );
        assertEquals( "stomp-frame-decoder", handlerNames.get( 5 ) );
    }

    protected List<String> getHandlerNames() {
        ChannelPipeline pipeline = this.pipelineExposer.getPipeline();
        List<String> names = new ArrayList<String>();
        names.addAll( pipeline.toMap().keySet() );
        return names;
    }

    protected void assertContains(String handlerName, ChannelPipeline pipeline) {
        assertTrue( pipeline.toMap().containsKey( handlerName ) );
    }
}
