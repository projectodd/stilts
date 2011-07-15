package org.projectodd.stilts.stomp.protocol;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;

public class StompFrameEncoderTest {
    

    private EncoderEmbedder<ChannelBuffer> encoder;

    @Before
    public void setUp() {
        this.encoder = new EncoderEmbedder<ChannelBuffer>( new StompFrameEncoder() );
    }
    
    @Test
    public void testAckFrame() {
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        frame.setHeader( Header.SUBSCRIPTION, "subscription-42" );
        frame.setHeader( Header.MESSAGE_ID, "message-84" );
        
        boolean result = this.encoder.offer( frame );
        assertTrue( result );
        
        List<String> lines = getFrameLines();
        
        assertEquals( "ACK", lines.get(0) );
        assertContainsLine( "subscription:subscription-42", lines );
        assertContainsLine( "message-id:message-84", lines );
    }
    
    @Test
    public void testConnectedFrame() {
        StompFrame frame = StompFrames.newConnectedFrame( "session-42", Version.VERSION_1_1 );
        
        boolean result = this.encoder.offer( frame );
        assertTrue( result );
        
        List<String> lines = getFrameLines();
        
        assertEquals( "CONNECTED", lines.get(0) );
        assertContainsLine( "session:session-42", lines );
        assertContainsLine( "version:1.1", lines );
    }
    
    @Test
    public void testErrorFrame() {
        StompMessage message = StompMessages.createStompMessage();
        message.getHeaders().put( Header.RECEIPT, "some-frame" );
        StompFrame inReplyTo = StompFrames.newSendFrame( message );
        StompFrame frame = StompFrames.newErrorFrame( "It broke", inReplyTo );
        
        boolean result = this.encoder.offer( frame );
        assertTrue( result );
        
        List<String> lines = getFrameLines();
        
        assertEquals( "ERROR", lines.get(0) );
        assertContainsLine( "receipt-id:some-frame", lines );
        assertContainsContent( "It broke", lines );
    }
    
    protected void assertContainsLine(String expected, List<String> lines) {
        assertTrue( lines.indexOf( expected ) >= 0 );
    }
    
    protected void assertContainsContent(String expected, List<String> lines) {
        String actual = lines.get( lines.size()-1 );
        assertEquals( expected, actual.substring( 0, expected.length() ) );
        byte[] bytes = actual.getBytes();
        assertEquals( 0, bytes[ bytes.length - 1 ] );
    }
    
    protected List<String> getFrameLines() {
        ChannelBuffer buffer = this.encoder.poll();
        assertNotNull( buffer );
        
        List<String> lines = new ArrayList<String>();
        
        String content = buffer.toString( Charset.forName( "UTF-8"  ) );
        
        StringTokenizer tokenizer = new StringTokenizer( content, "\n"  );
        
        while ( tokenizer.hasMoreTokens() ) {
            lines.add(  tokenizer.nextToken() );
        }
        
        return lines;
        
    }

}
