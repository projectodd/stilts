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
package org.projectodd.stilts.stomplet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.projectodd.stilts.conduit.stomp.SimpleStompSessionManager;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.server.StompletServer;

/**
 * A test that verifies that the client can connect if
 * the server gets srtarted after the connection attempt.
 * 
 * @author thomas.diesler@jboss.com
 * @since 12-Sep-2011
 */
public class ClientConnectionTest {

    private ExecutorService executor = Executors.newCachedThreadPool();
    
    private String getConnectionUrl() {
        return "stomp://localhost/";
    }

    @Test
    public void testClientConnection() throws Exception {

        // Start a client connection in a seperate thread
        final StompClient client = new StompClient(getConnectionUrl());
        Callable<Boolean> callable = new Callable<Boolean>() {
            public Boolean call() throws Exception {
                client.connect();
                State state = client.getConnectionState();
                return state == State.CONNECTED;
            }
        };
        Future<Boolean> future = executor.submit(callable);
        
        // Wait for the client to reach state CONNECTING 
        long timeout = 2000;
        while(client.getConnectionState() != State.CONNECTING && timeout > 0) {
            Thread.sleep(100);
            timeout -= 200;
        } 
        assertEquals(State.CONNECTING, client.getConnectionState());
        
        // Start the server while the client is in state CONNECTING
        StompletServer server = new StompletServer();
        try 
        {
            server.setDefaultContainer(new SimpleStompletContainer());
            server.setDefaultSessionManager(new SimpleStompSessionManager());
            server.start();
            
            // Verify that the client got connected
            Boolean connected = future.get();
            assertTrue("Client connected", connected);
        }
        finally {
            server.stop();
        }
    }
}
