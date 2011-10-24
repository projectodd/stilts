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

import java.net.InetAddress;

import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.projectodd.stilts.conduit.ConduitServer;
import org.projectodd.stilts.conduit.spi.StompSessionManager;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomplet.container.StompletContainer;
import org.projectodd.stilts.stomplet.container.StompletMessageConduitFactory;

/**
 * Virtual-hosting server supporting <code>StompletContainers</code>.
 *
 * @author Bob McWhirter
 */
public class StompletServer {

    private static Logger log = Logger.getLogger(StompletServer.class);

    public StompletServer() {
        this( Constants.DEFAULT_PORT );
    }

    public StompletServer(int port) {
        this.server = new ConduitServer<StompletMessageConduitFactory>( port );
        this.server.setMessageConduitFactory( new StompletMessageConduitFactory() );
    }
    
    public void setPort(int port) {
    	this.server.setPort( port );
    }
    
    public int getPort() {
    	return this.server.getPort();
    }
    
    public void setBindAddress(InetAddress bindAddress) {
    	this.server.setBindAddress( bindAddress );
    }
    
    public InetAddress getBindAddress() {
    	return this.server.getBindAddress();
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
