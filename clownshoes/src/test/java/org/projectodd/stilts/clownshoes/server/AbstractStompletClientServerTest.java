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
import org.projectodd.stilts.clownshoes.server.StandaloneStompletCircusServer;
import org.projectodd.stilts.clownshoes.server.StompletCircusServer;
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
