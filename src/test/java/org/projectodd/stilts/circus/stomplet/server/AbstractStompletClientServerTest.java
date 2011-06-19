/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

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
