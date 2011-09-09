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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.conduit.spi.StompSessionManager;
import org.projectodd.stilts.conduit.spi.TransactionalMessageConduitFactory;
import org.projectodd.stilts.conduit.stomp.SimpleStompSessionManager;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.StompSession;

public class StompletMessageConduitFactory implements TransactionalMessageConduitFactory {

    private static Logger log = Logger.getLogger(StompletMessageConduitFactory.class);

    public void setTransactionManager(TransactionManager transactionManager) {
        log.debugf( "setTransactionManager: %s", transactionManager );
        this.transactionManager = transactionManager;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        String host = headers.get( Header.HOST );

        StompletContainer container = null;

        log.debugf( "looking for container for host: %s", host );

        if (host != null) {
            container = findStompletContainer( host );
        }

        if (container == null) {
            container = findStompletContainer( "localhost" );
        }

        if ( container == null ) {
            container = this.defaultContainer;
        }

        if (container == null) {
            throw new NoSuchHostException( host );
        }

        StompSessionManager sessionManager = findSessionManager( host );

        if ( sessionManager == null ) {
            sessionManager = findSessionManager( "localhost" );
        }

        if ( sessionManager == null ) {
            sessionManager = this.defaultSessionManager;
        }

        String sessionId = headers.get( Header.SESSION );

        StompSession session = null;

        if (sessionId != null) {
            session = sessionManager.findSession( sessionId );
        }

        if ( session == null ) {
            session = sessionManager.createSession();
        }

        return new StompletMessageConduit( this.transactionManager, container, messageSink, session );
    }

    public void registerVirtualHost(final String host, final StompletContainer container, StompSessionManager sessionManager) {
        this.virtualHosts.put( host.toLowerCase(), container );
        if (sessionManager == null) {
            sessionManager = new SimpleStompSessionManager();
        }
        this.sessionManagers.put( host.toLowerCase(), sessionManager );
    }

    public void unregisterVirtualHost(final String host) {
        this.virtualHosts.remove( host.toLowerCase() );
        this.sessionManagers.remove( host.toLowerCase() );
    }

    public StompletContainer findStompletContainer(final String host) {
        return this.virtualHosts.get( host.toLowerCase() );
    }

    public StompSessionManager findSessionManager(final String host) {
        return this.sessionManagers.get( host.toLowerCase() );
    }

    public void setDefaultContainer(StompletContainer container) {
        this.defaultContainer = container;
    }

    public void setDefaultSessionManager(StompSessionManager sessionManager) {
        this.defaultSessionManager = sessionManager;
    }

    public StompletContainer getDefaultContainer() {
        return this.defaultContainer;
    }

    public void start() throws Exception {
        Set<StompletContainer> containers = new HashSet<StompletContainer>();
        containers.addAll( this.virtualHosts.values() );
        if (this.defaultContainer != null) {
            containers.add( this.defaultContainer );
        }

        for (StompletContainer each : containers) {
            each.start();
        }
    }

    public void stop() throws Exception {
        Set<StompletContainer> containers = new HashSet<StompletContainer>();
        containers.addAll( this.virtualHosts.values() );
        if (this.defaultContainer != null) {
            containers.add( this.defaultContainer );
        }

        for (StompletContainer each : containers) {
            each.stop();
        }
    }

    private TransactionManager transactionManager;
    private Map<String, StompletContainer> virtualHosts = new HashMap<String, StompletContainer>();
    private Map<String, StompSessionManager> sessionManagers = new HashMap<String, StompSessionManager>();
    private StompletContainer defaultContainer = null;
    private StompSessionManager defaultSessionManager = null;

}
