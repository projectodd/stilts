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

package org.projectodd.stilts.stomplet.server;

import java.util.List;
import java.util.concurrent.Executor;

import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.projectodd.stilts.conduit.ConduitServer;
import org.projectodd.stilts.conduit.spi.StompSessionManager;
import org.projectodd.stilts.stomp.server.Connector;
import org.projectodd.stilts.stomp.server.Server;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomplet.container.StompletContainer;
import org.projectodd.stilts.stomplet.container.StompletMessageConduitFactory;

/**
 * Virtual-hosting server supporting <code>StompletContainers</code>.
 *
 * @author Bob McWhirter
 */
public class StompletServer implements Server {

    private static Logger log = Logger.getLogger(StompletServer.class);

    public StompletServer() {
        this.server = new ConduitServer<StompletMessageConduitFactory>();
        this.server.setMessageConduitFactory( new StompletMessageConduitFactory() );
    }
    
    public void addConnector(Connector connector) throws Exception {
        this.server.addConnector( connector);
    }
    
    public List<Connector> getConnectors() {
        return this.server.getConnectors();
    }
    
    public void removeConnector(Connector connector) throws Exception {
        this.server.removeConnector( connector );
    }
    
    public StompProvider getStompProvider() {
        return this.server.getStompProvider();
    }
    
    public Executor getMessageHandlingExecutor() {
        return this.server.getMessageHandlingExecutor();
    }
    
    public void setTransactionManager(TransactionManager transactionManager) {
        this.server.setTransactionManager( transactionManager );
    }

    public TransactionManager getTransactionManager() {
        return this.server.getTransactionManager();
    }

    public void registerVirtualHost(String host, StompletContainer container, StompSessionManager sessionManager) {
        this.server.getMessageConduitFactory().registerVirtualHost( host, container, sessionManager );
    }

    public void unregisterVirtualHost(String host) {
        this.server.getMessageConduitFactory().unregisterVirtualHost( host );
    }

    public void setDefaultContainer(StompletContainer container) {
        this.server.getMessageConduitFactory().setDefaultContainer( container );
    }

    public void setDefaultSessionManager(StompSessionManager sessionManager) {
        this.server.getMessageConduitFactory().setDefaultSessionManager( sessionManager );
    }

    public void start() throws Exception {
        this.server.start();
    }

    public void stop() throws Exception {
        this.server.stop();
    }

    private ConduitServer<StompletMessageConduitFactory> server;

}
