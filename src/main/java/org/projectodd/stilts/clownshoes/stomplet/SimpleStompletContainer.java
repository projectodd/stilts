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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.stomplet.MessageRouter;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.StompletConfig;

public class SimpleStompletContainer implements StompletContainer, MessageRouter {

    public SimpleStompletContainer() {
    }
    
    public void start() throws Exception {
        this.stompletContext = new DefaultStompletContext( this );
    }
    
    public void stop() throws Exception {
        while ( ! this.routes.isEmpty() ) {
            Route route = this.routes.remove( 0 );
            destroy( route.getStomplet() );
        }
    }
    
    protected void destroy(Stomplet stomplet) throws StompException {
        stomplet.destroy();
    }
    
    public void addStomplet(String destinationPattern, Stomplet stomplet) throws StompException {
        StompletConfig config = new DefaultStompletConfig( this.stompletContext, null );
        stomplet.initialize( config );
        Route route = new Route( destinationPattern, stomplet );
        System.err.println( "*** INSTALL ROUTE " + destinationPattern + " // " + route );
        this.routes.add( route );
    }

    @Override
    public void send(StompMessage message) throws StompException {
        RouteMatch match = match( message.getDestination() );

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

    public RouteMatch match(String destination) {
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


    private DefaultStompletContext stompletContext;
    private final List<Route> routes = new ArrayList<Route>();

}
