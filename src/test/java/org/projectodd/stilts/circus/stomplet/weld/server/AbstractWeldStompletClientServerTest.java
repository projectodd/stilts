/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.circus.stomplet.weld.server;

import org.projectodd.stilts.circus.AbstractStandaloneClientServerTest;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.circus.stomplet.server.StompletCircusServer;
import org.projectodd.stilts.circus.stomplet.weld.CircusBeanDeploymentArchive;

public abstract class AbstractWeldStompletClientServerTest extends AbstractStandaloneClientServerTest<StompletCircusServer> {

    @Override
    public StandaloneCircusServer<StompletCircusServer> createServer() throws Exception {
        StompletCircusServer server = new StompletCircusServer();
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
