package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.*;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.server.MockStompProvider;

public class AckHandlerTest {
    
    private MockStompProvider stompProvider;
    private DecoderEmbedder<Object> handler;
    private ConnectionContext connectionContext;
    
    @Before
    public void setUp() throws StompException {
        this.stompProvider = new MockStompProvider();
        this.connectionContext = new ConnectionContext();
        this.connectionContext.setStompConnection( this.stompProvider.createConnection( null, null ) );
        this.handler = new DecoderEmbedder<Object>( new AckHandler( this.stompProvider, this.connectionContext ) );
    }
    
    @Test
    public void testAckWithoutTransaction() {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        this.connectionContext.getAckManager().registerAcknowledger( "message-98", acknowledger );
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        frame.setHeader( Header.SUBSCRIPTION, "subscription-44" );
        frame.setHeader( Header.MESSAGE_ID, "message-98" );
        
        this.handler.offer( frame );
        
        assertEquals( 1, acknowledger.getAcks().size() );
        assertEquals( 0, acknowledger.getNacks().size() );
        
        assertNull( acknowledger.getAcks().get(0) );
    }
    
    @Test
    public void testAckWithTransaction() {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        this.connectionContext.getAckManager().registerAcknowledger( "message-98", acknowledger );
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        frame.setHeader( Header.SUBSCRIPTION, "subscription-44" );
        frame.setHeader( Header.MESSAGE_ID, "message-98" );
        frame.setHeader( Header.TRANSACTION, "transaction-bob" );
        
        this.handler.offer( frame );
        
        assertEquals( 1, acknowledger.getAcks().size() );
        assertEquals( 0, acknowledger.getNacks().size() );
        
        assertEquals( "transaction-bob", acknowledger.getAcks().get(0) );
    }

}
