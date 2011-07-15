package org.projectodd.stilts.stomp.protocol;

import static org.junit.Assert.*;

import java.nio.charset.Charset;

import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class StompMessageEncoderTest {

    private EncoderEmbedder<StompContentFrame> encoder;

    @Before
    public void setUp() {
        this.encoder = new EncoderEmbedder<StompContentFrame>( new StompMessageEncoder() );
    }

    @Test
    public void testNonMessage() throws Exception {
        boolean result = this.encoder.offer( "Howdy" );
        assertFalse( result );
    }

    @Test
    public void testMessage() throws Exception {
        StompMessage message = StompMessages.createStompMessage( "/queues/one", "sasquatch!" );
        boolean result = this.encoder.offer( message );
        assertTrue( result );
        
        StompContentFrame frame = this.encoder.poll();
        assertNotNull( frame );
        
        assertEquals( "/queues/one", frame.getHeader( Header.DESTINATION ) );
        assertEquals( "sasquatch!", frame.getContent().toString( Charset.forName( "UTF-8" ) ) );
        
    }
}
