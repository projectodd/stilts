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

package org.projectodd.stilts.conduit;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.stomp.client.SimpleStompClient;
import org.projectodd.stilts.stomp.client.helpers.MessageAccumulator;
import org.projectodd.stilts.stomp.server.SimpleStompServer;

public abstract class AbstractCircusClientServerTest<SERVER extends ConduitServer<?>> {

    private TransactionManager transactionManager;
    private SERVER server;
    protected SimpleStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }

    @Before
    public void startServer() throws Throwable {
        setUpTransactionManager();
        this.server = createServer();
        this.server.setTransactionManager( this.transactionManager );
        this.server.start();
        prepareServer();
    }
    
    protected abstract SERVER createServer() throws Exception;
    
    public void prepareServer() throws Exception {
    }
    
    protected void setUpTransactionManager() {
        //this.transactionManager = jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }
    
    public SERVER getServer() {
        return this.server;
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
