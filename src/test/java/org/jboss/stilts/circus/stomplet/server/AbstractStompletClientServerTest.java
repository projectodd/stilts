package org.jboss.stilts.circus.stomplet.server;

import org.jboss.stilts.circus.AbstractCircusClientServerTest;
import org.jboss.stilts.circus.server.StompletCircusServer;
import org.jboss.stilts.circus.stomplet.RouteConfig;
import org.jboss.stilts.circus.stomplet.StompletContainer;
import org.jboss.stilts.stomplet.simple.SimpleQueueStomplet;
import org.jboss.stilts.stomplet.simple.SimpleTopicStomplet;

public abstract class AbstractStompletClientServerTest extends AbstractCircusClientServerTest {

    @Override
    public StompletCircusServer createServer() throws Exception {
        StompletCircusServer server = new StompletCircusServer();
        server.setLoggerManager( this.serverLoggerManager );

        StompletContainer container = new StompletContainer( getClass().getClassLoader() );
        server.setStompletContainer( container );

        RouteConfig queuesRouteConfig = new RouteConfig();
        queuesRouteConfig.setClassName( SimpleQueueStomplet.class.getName() );
        queuesRouteConfig.setPattern( "/queues/:destination" );
        container.addRoute( queuesRouteConfig );

        RouteConfig topicsRouteConfig = new RouteConfig();
        topicsRouteConfig.setClassName( SimpleTopicStomplet.class.getName() );
        topicsRouteConfig.setPattern( "/topics/:destination" );
        container.addRoute( topicsRouteConfig );

        return server;
    }

}
