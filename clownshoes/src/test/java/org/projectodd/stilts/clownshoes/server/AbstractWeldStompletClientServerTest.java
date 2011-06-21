/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.clownshoes.server;

import org.projectodd.stilts.circus.AbstractStandaloneClientServerTest;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.clownshoes.server.StandaloneWeldStompletCircusServer;
import org.projectodd.stilts.clownshoes.server.StompletCircusServer;
import org.projectodd.stilts.clownshoes.weld.CircusBeanDeploymentArchive;

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
