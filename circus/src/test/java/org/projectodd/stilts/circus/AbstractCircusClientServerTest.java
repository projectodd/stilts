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

import javax.transaction.TransactionManager;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.MessageAccumulator;
import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.logging.SimpleLoggerManager;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;
import org.projectodd.stilts.server.SimpleStompServer;
import org.projectodd.stilts.stomp.client.AbstractStompClient;

//import com.arjuna.ats.jta.common.jtaPropertyManager;

public abstract class AbstractCircusClientServerTest<T extends CircusServer> {

    public static Level SERVER_ROOT_LEVEL = Level.INFO;
    public static Level CLIENT_ROOT_LEVEL = Level.NONE;

    private TransactionManager transactionManager;
    private T server;
    protected SimpleLoggerManager serverLoggerManager;
    protected SimpleLoggerManager clientLoggerManager;
    protected AbstractStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }

    @Before
    public void startServer() throws Throwable {
        setUpServerLoggerManager();
        setUpTransactionManager();
        this.server = createServer();
        this.server.setTransactionManager( this.transactionManager );
        this.server.start();
        prepareServer();
    }
    
    protected void setUpTransactionManager() {
        //this.transactionManager = jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }
    
    public void setUpServerLoggerManager() {
        this.serverLoggerManager = new SimpleLoggerManager( System.err, "server" );
        this.serverLoggerManager.setRootLevel( SERVER_ROOT_LEVEL );
    }
    
    protected abstract T createServer() throws Exception;
    
    public void prepareServer() throws Exception {
        
    }
    
    public T getServer() {
        return this.server;
    }
                          

    @Before
    public void setUpClient() throws Exception {
        setUpClientLogger();
        InetSocketAddress address = new InetSocketAddress( "localhost", SimpleStompServer.DEFAULT_PORT );
        this.client = new AbstractStompClient( address );
        this.client.setLoggerManager( this.clientLoggerManager );
    }

    public void setUpClientLogger() {
        this.clientLoggerManager = new SimpleLoggerManager( System.err, "client" );
        this.clientLoggerManager.setRootLevel( CLIENT_ROOT_LEVEL );
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
