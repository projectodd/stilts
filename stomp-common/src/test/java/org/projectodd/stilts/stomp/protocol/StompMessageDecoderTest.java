package org.projectodd.stilts.stomp.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.DefaultStompMessageFactory;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;

public class StompMessageDecoderTest {

    
    private DecoderEmbedder<StompMessage> decoder;

    @Before
    public void setUp() {
        this.decoder = new DecoderEmbedder<StompMessage>( new StompMessageDecoder( DefaultStompMessageFactory.INSTANCE ) );
    }
    
    @Test
    public void testControlFrame() throws Exception {
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        boolean result = this.decoder.offer( frame );
        assertFalse( result );
    }
    
    @Test
    public void testContentFrame() throws Exception {
        StompMessage message = StompMessages.createStompMessage( "/queues/one", "wtf is a taco?" );
        StompFrame frame = StompFrames.newSendFrame( message );
        boolean result = this.decoder.offer( frame );
        assertTrue( result );     
        
        StompMessage outputMessage = this.decoder.poll();
        
        assertNotNull( outputMessage );
        
        assertNotSame( message, outputMessage );
        assertEquals( "/queues/one", outputMessage.getDestination() );
        assertEquals( "wtf is a taco?", outputMessage.getContentAsString() );
    }

    
}
