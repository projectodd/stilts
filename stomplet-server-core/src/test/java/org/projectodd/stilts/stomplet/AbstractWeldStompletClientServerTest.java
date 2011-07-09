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

import org.projectodd.stilts.circus.AbstractStandaloneClientServerTest;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.clownshoes.weld.CircusBeanDeploymentArchive;
import org.projectodd.stilts.stomplet.StompletServer;

public abstract class AbstractWeldStompletClientServerTest extends AbstractStandaloneClientServerTest<StompletServer> {

    @Override
    public StandaloneCircusServer<StompletServer> createServer() throws Exception {
        StompletServer server = new StompletServer();
        StandaloneWeldStompletCircusServer standalone = new StandaloneWeldStompletCircusServer( server );
        standalone.addBeanDeploymentArchive( getBeanDeploymentArchive() );
        return standalone;
    }
    
    public abstract CircusBeanDeploymentArchive getBeanDeploymentArchive() throws Exception;
    
    /*
    public void prepareServer() throws Exception {
        super.prepareServer();
        getServer().getStompletContainer().addStomplet( "/queues/:destination", new SimpleQueueStomplet() );
        getServer().getStompletContainer().addStomplet( "/topics/:destination", new SimpleTopicStomplet() );
    }
    */

}
