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

import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.StompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.StompletMessageConduitFactory;

public class StompletCircusServer extends CircusServer {

    public StompletCircusServer() {
    }
    
    public StompletCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startConduitFactory();
        this.stompletContainer.start();
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
        this.stompletContainer.stop();
    }
    
    protected void startConduitFactory() {
        StompletMessageConduitFactory factory = new StompletMessageConduitFactory( this.stompletContainer );
        PsuedoXAMessageConduitFactory xaFactory = new PsuedoXAMessageConduitFactory( factory );
        setMessageConduitFactory( xaFactory );
    }

    public void setStompletContainer(StompletContainer stompletContainer) {
        this.stompletContainer = stompletContainer;
    }
    
    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    private StompletContainer stompletContainer;

}
