package org.jboss.stilts.interop;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.jboss.stilts.client.AbstractStompClient;
import org.jboss.stilts.logging.SimpleLoggerManager;
import org.jboss.stilts.logging.SimpleLoggerManager.Level;
import org.jboss.stilts.stomplet.RouteConfig;
import org.jboss.stilts.stomplet.StompletContainer;
import org.jboss.stilts.stomplet.server.StompletServer;
import org.jboss.stilts.stomplet.simple.SimpleQueueStomplet;
import org.jboss.stilts.stomplet.simple.SimpleTopicStomplet;
import org.junit.After;
import org.junit.Before;

public class AbstractClientServerTest {

    public static Level SERVER_ROOT_LEVEL = Level.INFO;
    public static Level CLIENT_ROOT_LEVEL = Level.NONE;

    protected StompletServer server;
    protected SimpleLoggerManager serverLoggerManager;
    protected SimpleLoggerManager clientLoggerManager;
    protected AbstractStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }

    @Before
    public void startServer() throws Exception {
        setUpServerLoggerManager();
        this.server = new StompletServer();
        this.server.setLoggerManager( this.serverLoggerManager );

        StompletContainer container = this.server.getStompletContainer();

        RouteConfig queuesRouteConfig = new RouteConfig();
        queuesRouteConfig.setClassName( SimpleQueueStomplet.class.getName() );
        queuesRouteConfig.setPattern( "/queues/:destination" );
        container.addRoute( queuesRouteConfig );

        RouteConfig topicsRouteConfig = new RouteConfig();
        topicsRouteConfig.setClassName( SimpleTopicStomplet.class.getName() );
        topicsRouteConfig.setPattern( "/topics/:destination" );
        container.addRoute( topicsRouteConfig );

        this.server.start();
    }

    public void setUpServerLoggerManager() {
        this.serverLoggerManager = new SimpleLoggerManager( System.err, "server" );
        this.serverLoggerManager.setRootLevel( SERVER_ROOT_LEVEL );
    }

    @Before
    public void setUpClient() throws Exception {
        setUpClientLogger();
        InetSocketAddress address = new InetSocketAddress( "localhost", StompletServer.DEFAULT_PORT );
        this.client = new AbstractStompClient( address );
        this.client.setLoggerManager( this.clientLoggerManager );
    }

    public void setUpClientLogger() {
        this.clientLoggerManager = new SimpleLoggerManager( System.err, "client" );
        this.clientLoggerManager.setRootLevel( CLIENT_ROOT_LEVEL );
    }

    @After
    public void stopServer() {
        this.server.stop();
    }
    
    public MessageAccumulator accumulator(String name, boolean shouldAck, boolean shouldNack) {
        MessageAccumulator accumulator = this.accumulators.get( name );
        if (accumulator == null) {
            accumulator = new MessageAccumulator( shouldAck, shouldNack );
            this.accumulators.put( name, accumulator );
        }

        return accumulator;
    }

    public MessageAccumulator accumulator(String name) {
        return accumulator( name, false, false );
    }

}
