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

package org.projectodd.stilts.circus.jms;

import org.projectodd.stilts.circus.AbstractCircusClientServerTest;
import org.projectodd.stilts.circus.jms.DirectDestinationMapper;
import org.projectodd.stilts.circus.server.HornetQCircusServer;

public abstract class AbstractHornetQClientServerTest extends AbstractCircusClientServerTest<HornetQCircusServer> {

    public HornetQCircusServer createServer() throws Exception {
        HornetQCircusServer server = new HornetQCircusServer();
        server.setLoggerManager( this.serverLoggerManager );
        server.setDestinationMapper( DirectDestinationMapper.INSTANCE );
        return server;
    }
    
    public void prepareServer() throws Exception {
        getServer().addQueue( "/queues/foo" );
        getServer().addTopic( "/topics/foo" );
    }


}
