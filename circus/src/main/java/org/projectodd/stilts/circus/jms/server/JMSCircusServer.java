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

package org.projectodd.stilts.circus.jms.server;

import javax.jms.XAConnection;

import org.projectodd.stilts.circus.jms.DestinationMapper;
import org.projectodd.stilts.circus.jms.JMSMessageConduitFactory;
import org.projectodd.stilts.circus.server.CircusServer;

public class JMSCircusServer extends CircusServer {

    public JMSCircusServer() {
        super();
    }
    
    public JMSCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startConduitFactory();
        super.start();
    }
    
    protected void startConduitFactory() {
        JMSMessageConduitFactory factory = new JMSMessageConduitFactory( getConnection(), getDestinationMapper() );
        setMessageConduitFactory( factory );
    }
    
    public void setConnection(XAConnection connection) {
        this.connection = connection;
    }
    
    public XAConnection getConnection() {
        return this.connection;
    }
    
    public void setDestinationMapper(DestinationMapper destinationMapper) {
        this.destinationMapper = destinationMapper;
    }
    
    public DestinationMapper getDestinationMapper() {
        return this.destinationMapper;
    }

    private DestinationMapper destinationMapper;
    private XAConnection connection;

}
