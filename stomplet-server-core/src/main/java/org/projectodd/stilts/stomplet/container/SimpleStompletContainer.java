/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomplet.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
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
        addStomplet( destinationPattern, stomplet, new HashMap<String,String>() );
    }
    
    public void addStomplet(String destinationPattern, Stomplet stomplet, Map<String,String> properties) throws StompException {
        StompletConfig config = new DefaultStompletConfig( this.stompletContext, properties );
        stomplet.initialize( config );
        Route route = new Route( destinationPattern, stomplet );
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
        RouteMatch match = null;
        for (Route route : this.routes) {
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
