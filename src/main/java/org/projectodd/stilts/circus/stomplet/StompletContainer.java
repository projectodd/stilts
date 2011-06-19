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

package org.projectodd.stilts.circus.stomplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.stomplet.MessageRouter;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.StompletConfig;

public class StompletContainer implements MessageRouter {

    public StompletContainer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void start() throws StompException {
        System.err.println( "** START CONTAINER" );
        List<Route> installed = new ArrayList<Route>();

        for (RouteConfig each : this.routeConfigs) {
            try {
                installRoute( each );
            } catch (Exception e) {
                for (Route eachInstalled : installed) {
                    try {
                        eachInstalled.getStomplet().destroy();
                    } catch (StompException ignored) {
                        // ignore and log
                    }
                }
                throw new StompException( e );
            }
        }
    }

    public void stop() throws StompException {
        for (Route each : this.routes) {
            try {
                each.getStomplet().destroy();
            } catch (StompException ignored) {
                // ignore and log
            }
        }

        this.routes.clear();
    }

    @Override
    public void send(StompMessage message) throws StompException {
        System.err.println( "%%% container.send: " + message);
        RouteMatch match = match( message.getDestination() );
        System.err.println( "%%% container.send.match: " + match );

        if (match != null) {
            Stomplet stomplet = match.getRoute().getStomplet();
            Map<String, String> matches = match.getMatches();
            Headers headers = message.getHeaders();
            for (String name : matches.keySet()) {
                headers.put( "stomplet." + name, matches.get( name ) );
            }
            stomplet.onMessage( message );
        }
    }
    

    public void addRoute(RouteConfig routeConfig) {
        this.routeConfigs.add( routeConfig );
    }

    protected Route installRoute(RouteConfig routeConfig) throws Exception {
        Class<?> stompletClass = this.classLoader.loadClass( routeConfig.getClassName() );
        Stomplet stomplet = (Stomplet) stompletClass.newInstance();
        StompletConfig config = createStompletConfig( routeConfig );
        stomplet.initialize( config );
        return installRoute( routeConfig.getPattern(), stomplet );
    }

    protected StompletConfig createStompletConfig(RouteConfig routeSpec) {
        DefaultStompletConfig config = new DefaultStompletConfig( this.stompletContext, routeSpec.getProperties() );
        return config;
    }

    Route installRoute(String destinationPattern, Stomplet stomplet) {
        Route route = new Route( destinationPattern, stomplet );
        System.err.println( "*** INSTALL ROUTE " + destinationPattern + " // " + route );
        this.routes.add( route );
        return route;
    }

    RouteMatch match(String destination) {
        System.err.println( "match.destination=[" + destination + "]" );
        RouteMatch match = null;
        for (Route route : this.routes) {
            System.err.println( "attempt: " + route );
            match = route.match( destination );
            if (match != null) {
                break;
            }
        }

        return match;
    }

    private ClassLoader classLoader;
    private final DefaultStompletContext stompletContext = new DefaultStompletContext( this );

    private final List<RouteConfig> routeConfigs = new ArrayList<RouteConfig>();
    private final List<Route> routes = new ArrayList<Route>();

}
