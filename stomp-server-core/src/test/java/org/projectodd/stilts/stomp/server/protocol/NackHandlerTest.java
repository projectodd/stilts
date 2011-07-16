package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.AbstractStompServerTestCase;
import org.projectodd.stilts.stomp.server.MockStompProvider;
import org.projectodd.stilts.stomp.server.StompServer;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class NackHandlerTest extends AbstractStompServerTestCase<MockStompProvider> {

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        return server;
    }

    @Test
    public void testErrorVersion10() throws Exception {
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_0 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "NACK unsupported prior to STOMP 1.1.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testErrorNoMessageId() throws Exception {
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Cannot NACK without message ID.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testErrorNoSubscriptionId() throws Exception {
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        stompFrame.setHeader( Header.MESSAGE_ID, "1" );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Cannot NACK without subscription ID.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testInvalidMessageId() throws Exception {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        ctx.getAckManager().registerAcknowledger( "message-1", acknowledger );
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        stompFrame.setHeader( Header.MESSAGE_ID, "camp-krusty" );
        stompFrame.setHeader( Header.SUBSCRIPTION, "subscription-1" );
        handler.offer( stompFrame );
        StompFrame frame = (StompFrame) handler.poll();
        assertEquals( Command.NACK, frame.getCommand() );
        assertEquals( 0, acknowledger.getNacks().size() );
    }

    @Test
    public void testValidMessageIdNoTx() throws Exception {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        ctx.getAckManager().registerAcknowledger( "message-1", acknowledger );
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        stompFrame.setHeader( Header.MESSAGE_ID, "message-1" );
        stompFrame.setHeader( Header.SUBSCRIPTION, "subscription-1" );
        handler.offer( stompFrame );
        StompFrame frame = (StompFrame) handler.poll();
        assertEquals( Command.NACK, frame.getCommand() );
        assertEquals( 1, acknowledger.getNacks().size() );
        assertNull( acknowledger.getNacks().get( 0 ) );
    }

    @Test
    public void testValidMessageIdWithTx() throws Exception {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        ctx.getAckManager().registerAcknowledger( "message-1", acknowledger );
        StompConnection c = server.getStompProvider().createConnection( null, null, Version.VERSION_1_1 );
        ctx.setStompConnection( c );
        StompFrame stompFrame = new StompFrame( Command.NACK );
        stompFrame.setHeader( Header.MESSAGE_ID, "message-1" );
        stompFrame.setHeader( Header.SUBSCRIPTION, "subscription-1" );
        stompFrame.setHeader( Header.TRANSACTION, "tx-1" );
        handler.offer( stompFrame );
        StompFrame frame = (StompFrame) handler.poll();
        assertEquals( Command.NACK, frame.getCommand() );
        assertEquals( 1, acknowledger.getNacks().size() );
        assertEquals( "tx-1", acknowledger.getNacks().get( 0 ) );
    }    
    
    @After
    public void after() throws Exception {
        handler.finish();
    }

    @Before
    public void before() throws Exception {
        ctx = new ConnectionContext();
        handler = new DecoderEmbedder<StompFrame>( new NackHandler( server.getStompProvider(), ctx ) );
    }

    private DecoderEmbedder<StompFrame> handler;
    private ConnectionContext ctx;

}
