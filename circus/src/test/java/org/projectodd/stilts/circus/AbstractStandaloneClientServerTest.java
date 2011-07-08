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

package org.projectodd.stilts.circus;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.MessageAccumulator;
import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.server.SimpleStompServer;
import org.projectodd.stilts.stomp.client.SimpleStompClient;

public abstract class AbstractStandaloneClientServerTest<T extends CircusServer> {

    private StandaloneCircusServer<T> server;
    protected SimpleStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();
    
    @Before 
    public void beforeEverything() {
        
    }

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }
    
    
    protected abstract StandaloneCircusServer<T> createServer() throws Exception;

    @Before
    public void startServer() throws Throwable {
        this.server = createServer();
        this.server.start();
        prepareServer();
    }
    
    public void prepareServer() throws Exception {
        
    }
    
    public T getServer() {
        return this.server.getServer();
    }

    @Before
    public void setUpClient() throws Exception {
        InetSocketAddress address = new InetSocketAddress( "localhost", SimpleStompServer.DEFAULT_PORT );
        this.client = new SimpleStompClient( address );
    }

    @After
    public void stopServer() throws Throwable {
        this.server.stop();
    }
    
    @After 
    public void afterEverything() {
        
    }
    
    public MessageAccumulator accumulator(String name, boolean shouldAck, boolean shouldNack) {
        MessageAccumulator accumulator = this.accumulators.get( name );
        if (accumulator == null) {
            accumulator = new MessageAccumulator( shouldAck, shouldNack );
            this.accumulators.put( name, accumulator );
        }

        return accumulator;
    }

    public MessageAccumulator accumulator(String name) {
        return accumulator( name, false, false );
    }

}
