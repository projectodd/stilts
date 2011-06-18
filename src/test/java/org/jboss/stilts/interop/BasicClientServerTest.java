package org.jboss.stilts.interop;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.jboss.stilts.StompMessages;
import org.jboss.stilts.client.AbstractStompClient;
import org.jboss.stilts.client.ClientSubscription;
import org.jboss.stilts.client.ClientTransaction;
import org.jboss.stilts.logging.SimpleLoggerManager.Level;
import org.jboss.stilts.stomplet.server.StompletServer;
import org.junit.Test;

public class BasicClientServerTest extends AbstractClientServerTest {

    static {
        SERVER_ROOT_LEVEL = Level.NONE;
        CLIENT_ROOT_LEVEL = Level.NONE;
    }

    @Test
    public void testClient() throws Exception {
        client.connect();
        assertTrue( client.isConnected() );

        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator("one") ).start();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator("two") ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        for (int i = 0; i < 10; ++i) {
            client.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertEquals( 10, accumulator("one").size() );
        assertEquals( 10, accumulator("two").size() );
    }

    @Test
    public void testClientTransaction() throws Exception {
        InetSocketAddress address = new InetSocketAddress( "localhost", StompletServer.DEFAULT_PORT );
        AbstractStompClient client = new AbstractStompClient( address );
        client.setLoggerManager( this.clientLoggerManager );

        client.connect();
        assertTrue( client.isConnected() );

        MessageAccumulator accumulator1 = new MessageAccumulator();
        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator1 ).start();

        MessageAccumulator accumulator2 = new MessageAccumulator();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator2 ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );

        assertTrue( accumulator1.isEmpty() );
        assertTrue( accumulator2.isEmpty() );

        tx.commit();

        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertEquals( 10, accumulator1.size() );
        assertEquals( 10, accumulator2.size() );
    }

    @Test
    public void testClientTransactionAborted() throws Exception {
        InetSocketAddress address = new InetSocketAddress( "localhost", StompletServer.DEFAULT_PORT );
        AbstractStompClient client = new AbstractStompClient( address );
        client.setLoggerManager( this.clientLoggerManager );

        client.connect();
        assertTrue( client.isConnected() );

        MessageAccumulator accumulator1 = new MessageAccumulator();
        ClientSubscription subscription1 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator1 ).start();

        MessageAccumulator accumulator2 = new MessageAccumulator();
        ClientSubscription subscription2 = client.subscribe( "/topics/foo" ).withMessageHandler( accumulator2 ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );

        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        ClientTransaction tx = client.begin();

        for (int i = 0; i < 10; ++i) {
            tx.send( StompMessages.createStompMessage( "/topics/foo", "What? " + i ) );
        }

        Thread.sleep( 1000 );
        tx.abort();
        Thread.sleep( 1000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );

        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        assertTrue( accumulator1.isEmpty() );
        assertTrue( accumulator2.isEmpty() );
    }
}
