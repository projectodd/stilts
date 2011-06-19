package org.projectodd.stilts.circus.stomplet.server;

import org.projectodd.stilts.circus.AbstractCircusClientServerTest;
import org.projectodd.stilts.circus.server.StompletCircusServer;
import org.projectodd.stilts.circus.stomplet.RouteConfig;
import org.projectodd.stilts.circus.stomplet.StompletContainer;
import org.projectodd.stilts.stomplet.simple.SimpleQueueStomplet;
import org.projectodd.stilts.stomplet.simple.SimpleTopicStomplet;

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
