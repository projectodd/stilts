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
import javax.jms.XAConnectionFactory;
import javax.naming.InitialContext;

import org.projectodd.stilts.circus.server.StandaloneCircusServer;

public class StandaloneJMSCircusServer extends StandaloneCircusServer<JMSCircusServer> {


    public StandaloneJMSCircusServer(JMSCircusServer server) {
        super( server );
    }
    
    public void configure() throws Throwable {
        super.configure();
        
        InitialContext context = new InitialContext();
        XAConnectionFactory connectionFactory = (XAConnectionFactory) context.lookup( "java:/eis/hornetq-ra" );
        this.connection = connectionFactory.createXAConnection();
        getServer().setConnection( this.connection );
    }
    
    public void stop() throws Throwable {
        this.connection.close();
        this.connection = null;
        super.stop();
    }

    private XAConnection connection;
}
