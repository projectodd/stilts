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

package org.projectodd.stilts.conduit.stomp;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.conduit.spi.TransactionalMessageConduitFactory;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.server.helpers.OpenAuthenticator;
import org.projectodd.stilts.stomp.spi.Authenticator;
import org.projectodd.stilts.stomp.spi.StompConnection;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomp.spi.TransactionalAcknowledgeableMessageSink;

public class ConduitStompProvider implements StompProvider {

    public ConduitStompProvider(TransactionManager transactionManager, TransactionalMessageConduitFactory messageConduitFactory) {
        this( transactionManager, messageConduitFactory, null );
    }

    public ConduitStompProvider(TransactionManager transactionManager, TransactionalMessageConduitFactory messageConduitFactory, Authenticator authenticator) {
        this.transactionManager = transactionManager;
        if (authenticator == null) {
            authenticator = OpenAuthenticator.INSTANCE;
        }
        this.authenticator = authenticator;
        this.messageConduitFactory = messageConduitFactory;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public StompConnection createConnection(TransactionalAcknowledgeableMessageSink messageSink, Headers headers, Version version, Heartbeat hb) throws StompException {
        if (this.authenticator.authenticate( headers )) {
            try {
                ConduitStompConnection connection = createStompConnection( messageSink, getNextSessionId(), headers, version, hb );
                synchronized (this.connections) {
                    this.connections.add( connection );
                }
                return connection;
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
        return null;
    }

    public void stop() throws Exception {
        HashSet<ConduitStompConnection> disconnecting = new HashSet<ConduitStompConnection>();
        synchronized (this.connections) {
            disconnecting.addAll( this.connections );
        }
        for (ConduitStompConnection each : disconnecting) {
            each.disconnect();
        }
    }

    void unregister(ConduitStompConnection circusStompConnection) {
        synchronized (this.connections) {
            this.connections.remove( circusStompConnection );
        }
    }

    protected String getNextSessionId() {
        return "session-" + sessionCounter.getAndIncrement();
    }

    protected ConduitStompConnection createStompConnection(TransactionalAcknowledgeableMessageSink messageSink, String sessionId, Headers headers, Version version, Heartbeat hb)
            throws Exception {
        ConduitAcknowledgeableMessageSink conduitSink = new ConduitAcknowledgeableMessageSink( messageSink );
        ConduitStompConnection connection = new ConduitStompConnection( this, this.messageConduitFactory.createMessageConduit( conduitSink, headers ), sessionId, version, hb );
        conduitSink.setConnection( connection );
        return connection;
    }

    TransactionalMessageConduitFactory getMessageConduitFactory() {
        return this.messageConduitFactory;
    }

    private TransactionalMessageConduitFactory messageConduitFactory;
    private TransactionManager transactionManager;
    private Authenticator authenticator;
    private AtomicInteger sessionCounter = new AtomicInteger();
    private Set<ConduitStompConnection> connections = new HashSet<ConduitStompConnection>();

}
