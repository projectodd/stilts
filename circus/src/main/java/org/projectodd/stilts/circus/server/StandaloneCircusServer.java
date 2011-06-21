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

package org.projectodd.stilts.circus.server;

import java.net.URL;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.logging.SimpleLoggerManager;

public class StandaloneCircusServer<T extends CircusServer> {

	public StandaloneCircusServer(T server) {
        this.server = server;
    }

    public void start() throws Throwable {
        startEmbeddedJCA();
        configure();
        this.server.start();
    }
    
    public void setLoggerManager(LoggerManager loggerManager) {
    	this.loggerManager = loggerManager;
    }
    
    public LoggerManager getLoggerManager() {
    	return this.loggerManager;
    }

    public void configure() throws Throwable {
        this.server.setExecutor( Executors.newFixedThreadPool( 4 ) );
        this.server.setLoggerManager( getLoggerManager() );
        
        this.context = new InitialContext();
        TransactionManager transactionManager = (TransactionManager) context.lookup( "java:/TransactionManager" );
        this.server.setTransactionManager( transactionManager );
    }

    private void startEmbeddedJCA() throws Throwable {
        System.setProperty( "java.naming.factory.initial", "org.jnp.interfaces.LocalOnlyContextFactory" );
        System.setProperty( "java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" );
        this.embeddedJca = EmbeddedFactory.create( true );
        this.embeddedJca.startup();
    }
    
    protected void deployResourceAdapter(URL url) throws Throwable {
        this.embeddedJca.deploy( url );
    }

    public void stop() throws Throwable {
        this.server.stop();
        this.server.setTransactionManager( null );
        stopEmbeddedJCA();
    }

    private void stopEmbeddedJCA() throws Throwable {
        context.close();
        this.embeddedJca.shutdown();
        this.embeddedJca = null;
    }

    public T getServer() {
        return this.server;
    }
    

    private T server;
    private Embedded embeddedJca;
    private InitialContext context;
    private LoggerManager loggerManager = SimpleLoggerManager.DEFAULT_INSTANCE;

}
