package org.jboss.stilts.interop;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.jboss.stilts.StompMessage;
import org.jboss.stilts.StompMessages;
import org.jboss.stilts.client.AbstractStompClient;
import org.jboss.stilts.client.ClientSubscription;
import org.jboss.stilts.client.MessageHandler;
import org.jboss.stilts.logging.SimpleLoggerManager;
import org.jboss.stilts.logging.SimpleLoggerManager.Level;
import org.jboss.stilts.stomplet.RouteConfig;
import org.jboss.stilts.stomplet.StompletContainer;
import org.jboss.stilts.stomplet.server.StompletServer;
import org.jboss.stilts.stomplet.simple.SimpleQueueStomplet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientServerTest {

    private Level SERVER_ROOT_LEVEL = Level.NONE;
    private Level CLIENT_ROOT_LEVEL = Level.TRACE;

    private StompletServer server;
    private SimpleLoggerManager serverLoggerManager;
    private SimpleLoggerManager clientLoggerManager;

    @Before
    public void startServer() throws Exception {
        setUpServerLoggerManager();
        this.server = new StompletServer();
        this.server.setLoggerManager( this.serverLoggerManager );

        StompletContainer container = this.server.getStompletContainer();

        RouteConfig routeConfig = new RouteConfig();
        routeConfig.setClassName( SimpleQueueStomplet.class.getName() );
        routeConfig.setPattern( "/queues/:destination" );
        container.addRoute( routeConfig );

        this.server.start();
    }

    public void setUpServerLoggerManager() {
        this.serverLoggerManager = new SimpleLoggerManager( System.err, "server" );
        this.serverLoggerManager.setRootLevel( SERVER_ROOT_LEVEL );
    }

    @Before
    public void setUpClientLogger() {
        this.clientLoggerManager = new SimpleLoggerManager( System.err, "client" );
        this.clientLoggerManager.setRootLevel( CLIENT_ROOT_LEVEL );
    }

    @After
    public void stopServer() {
        this.server.stop();
    }

    @Test
    public void testClient() throws Exception {
        InetSocketAddress address = new InetSocketAddress( "localhost", StompletServer.DEFAULT_PORT );
        AbstractStompClient client = new AbstractStompClient( address );
        client.setLoggerManager( this.clientLoggerManager );

        client.connect();
        assertTrue( client.isConnected() );

        MessageAccumulator accumulator1 = new MessageAccumulator();
        ClientSubscription subscription1 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator1 ).start();
        
        MessageAccumulator accumulator2 = new MessageAccumulator();
        ClientSubscription subscription2 = client.subscribe( "/queues/foo" ).withMessageHandler( accumulator2 ).start();

        assertNotNull( subscription1 );
        assertTrue( subscription1.isActive() );
        
        assertNotNull( subscription2 );
        assertTrue( subscription2.isActive() );

        for (int i = 0; i < 100; ++i) {
            client.send( StompMessages.createStompMessage( "/queues/foo", "What? " + i ) );
        }

        Thread.sleep( 2000 );

        subscription1.unsubscribe();
        assertFalse( subscription1.isActive() );
        
        subscription2.unsubscribe();
        assertFalse( subscription2.isActive() );

        client.disconnect();
        assertTrue( client.isDisconnected() );

        System.err.println( "Accumulated-1: " + accumulator1.getMessage().size() );
        System.err.println( "Accumulated-2: " + accumulator2.getMessage().size() );
    }

    public static class MessageAccumulator implements MessageHandler {

        private ArrayList<StompMessage> messages;

        MessageAccumulator() {
            this.messages = new ArrayList<StompMessage>();
        }

        public void handle(StompMessage message) {
            System.err.println( "===>" + message );
            this.messages.add( message );
        }

        public List<StompMessage> getMessage() {
            return this.messages;
        }

        public void clear() {
            this.messages.clear();
        }

        public String toString() {
            return this.messages.toString();
        }

    }

}
