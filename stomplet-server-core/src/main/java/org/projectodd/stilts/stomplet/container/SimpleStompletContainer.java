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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.projectodd.stilts.stomp.InvalidDestinationException;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.spi.StompSession;
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

    public synchronized void addStomplet(String destinationPattern, Stomplet stomplet, Map<String, String> properties) throws StompException {
        StompletConfig config = new DefaultStompletConfig( this.stompletContext, properties );
        XAStomplet xaStomplet = null;
        if (stomplet instanceof XAStomplet) {
            xaStomplet = (XAStomplet) stomplet;
        } else {
            xaStomplet = new PseudoXAStomplet( stomplet );
        }
        xaStomplet.initialize( config );
        Route route = new Route( destinationPattern, xaStomplet );
        this.routes.add( route );
    }

    public synchronized void removeStomplet(String destinationPattern) throws StompException {
        Iterator<Route> iterator = routes.iterator();
        while(iterator.hasNext()) {
            Route route = iterator.next();
            if (route.getPatternString().equals(destinationPattern)) {
                route.getStomplet().destroy();
                iterator.remove();
                break;
            }
        } 
    }

    public void send(StompMessage message, StompSession session) throws StompException {
        StompletActivator activator = getActivator( message.getDestination() );
        if (activator == null) {
            throw new InvalidDestinationException( message.getDestination() );
        }
        activator.send( message, session );
    }

    @Override
    public StompletActivator getActivator(String destination) {
        //log.tracef(  "locate activator for destination: [%s]", destination );
        StompletActivator activator = null;
        for (Route route : this.routes) {
            //log.tracef(  "test route: %s", route );
            activator = route.match( destination );
            if (activator != null) {
                log.tracef( "matched route: %s", route );
                break;
            }
        }

        return activator;
    }

    private static Logger log = Logger.getLogger(SimpleStompletContainer.class);
    private DefaultStompletContext stompletContext;
    private final List<Route> routes = new ArrayList<Route>();

}
