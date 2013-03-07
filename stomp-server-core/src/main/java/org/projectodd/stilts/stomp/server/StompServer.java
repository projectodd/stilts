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

package org.projectodd.stilts.stomp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.projectodd.stilts.stomp.server.protocol.resource.ResourceManager;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompServer<T extends StompProvider> implements Server {

    public StompServer() {
    }

    public void setMessageHandlingExecutor(Executor executor) {
        this.messageHandlingExecutor = executor;
    }

    public Executor getMessageHandlingExecutor() {
        return this.messageHandlingExecutor;
    }

    public void setStompProvider(T stompProvider) {
        this.stompProvider = stompProvider;
    }

    public T getStompProvider() {
        return this.stompProvider;
    }
    
    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public synchronized void addConnector(Connector connector) throws Exception {
        if (this.connectors.contains( connector )) {
            return;
        }
        this.connectors.add( connector );
        if (this.running) {
            connector.start();
        }
    }

    public List<Connector> getConnectors() {
        return this.connectors;
    }

    public synchronized void removeConnector(Connector connector) throws Exception {
        if (this.connectors.remove( connector )) {
            if (this.running) {
                connector.stop();
            }
        }
    }

    /**
     * Start this server.
     * 
     * @throws Throwable
     * 
     */
    public synchronized void start() throws Exception {
        if (running) {
            return;
        }
        
        List<Connector> startedConnectors = new ArrayList<Connector>();
        for (Connector each : this.connectors) {
            try {
                each.setServer( this );
                each.start();
                startedConnectors.add( each );
            } catch (Exception e) {
                for (Connector stop : startedConnectors) {
                    try {
                        stop.stop();
                    } catch (Exception e2) {

                    }
                }
                throw e;
            }
        }
        running = true;
    }

    /**
     * Stop this server.
     * 
     * @throws Exception
     * @throws Throwable
     */
    public synchronized void stop() throws Exception {
        if (!running) {
            return;
        }

        for (Connector each : this.connectors) {
            try {
                each.stop();
                each.setServer( null );
            } catch (Exception e) {
                // ignore
            }
        }

        running = false;
    }

    private boolean running;
    private T stompProvider;
    private Executor messageHandlingExecutor;
    private List<Connector> connectors = new ArrayList<Connector>();
    private ResourceManager resourceManager;

}
