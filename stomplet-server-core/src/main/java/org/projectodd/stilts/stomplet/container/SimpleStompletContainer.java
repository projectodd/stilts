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
import java.util.Set;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.projectodd.stilts.stomp.InvalidDestinationException;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomplet.MessageRouter;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.StompletConfig;
import org.projectodd.stilts.stomplet.XAStomplet;
import org.projectodd.stilts.stomplet.container.xa.PseudoXAStomplet;

public class SimpleStompletContainer implements StompletContainer, MessageRouter {

    public SimpleStompletContainer() {
    }

    public void start() throws Exception {
        this.stompletContext = new DefaultStompletContext( this );
    }

    public void stop() throws Exception {
        while (!this.routes.isEmpty()) {
            Route route = this.routes.remove( 0 );
            destroy( route.getStomplet() );
        }
    }

    protected void destroy(Stomplet stomplet) throws StompException {
        stomplet.destroy();
    }

    public void addStomplet(String destinationPattern, Stomplet stomplet) throws StompException {
        addStomplet( destinationPattern, stomplet, new HashMap<String, String>() );
    }

    public void addStomplet(String destinationPattern, Stomplet stomplet, Map<String, String> properties) throws StompException {
        StompletConfig config = new DefaultStompletConfig( this.stompletContext, properties );
        stomplet.initialize( config );
        XAStomplet xaStomplet = null;
        if (stomplet instanceof XAStomplet) {
            xaStomplet = (XAStomplet) xaStomplet;
        } else {
            xaStomplet = new PseudoXAStomplet( stomplet );
        }
        Route route = new Route( destinationPattern, xaStomplet );
        this.routes.add( route );
    }

    public void send(StompMessage message) throws StompException {
        StompletActivator activator = getActivator( message.getDestination() );
        if (activator == null) {
            throw new InvalidDestinationException( message.getDestination() );
        }
        activator.send( message );
    }

    @Override
    public StompletActivator getActivator(String destination) {
        StompletActivator activator = null;
        for (Route route : this.routes) {
            activator = route.match( destination );
            if (activator != null) {
                break;
            }
        }

        return activator;
    }

    private DefaultStompletContext stompletContext;
    private final List<Route> routes = new ArrayList<Route>();

}
