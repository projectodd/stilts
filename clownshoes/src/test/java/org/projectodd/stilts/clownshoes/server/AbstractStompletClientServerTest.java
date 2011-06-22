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

package org.projectodd.stilts.clownshoes.server;

import org.projectodd.stilts.circus.AbstractStandaloneClientServerTest;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.simple.SimpleQueueStomplet;
import org.projectodd.stilts.stomplet.simple.SimpleTopicStomplet;

public abstract class AbstractStompletClientServerTest extends AbstractStandaloneClientServerTest<StompletCircusServer> {

    @Override
    public StandaloneCircusServer<StompletCircusServer> createServer() throws Exception {
        StompletCircusServer server = new StompletCircusServer();
        return new StandaloneStompletCircusServer( server );
    }
    
    protected SimpleStompletContainer getStompletContainer() {
        return (SimpleStompletContainer) getServer().getStompletContainer();
    }
    
    public void prepareServer() throws Exception {
        super.prepareServer();
        getStompletContainer().addStomplet( "/queues/:destination", new SimpleQueueStomplet() );
        getStompletContainer().addStomplet( "/topics/:destination", new SimpleTopicStomplet() );
    }

}
