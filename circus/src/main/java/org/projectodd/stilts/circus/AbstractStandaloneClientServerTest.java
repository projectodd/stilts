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

package org.projectodd.stilts.circus;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.projectodd.stilts.MessageAccumulator;
import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.logging.SimpleLoggerManager;
import org.projectodd.stilts.logging.SimpleLoggerManager.Level;
import org.projectodd.stilts.server.SimpleStompServer;
import org.projectodd.stilts.stomp.client.AbstractStompClient;

public abstract class AbstractStandaloneClientServerTest<T extends CircusServer> {
    
    public static Level SERVER_ROOT_LEVEL = Level.INFO;
    public static Level CLIENT_ROOT_LEVEL = Level.NONE;

    private StandaloneCircusServer<T> server;
    protected SimpleLoggerManager serverLoggerManager;
    protected SimpleLoggerManager clientLoggerManager;
    protected AbstractStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();
    
    @Before 
    public void beforeEverything() {
        
    }

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }
    
    
    protected abstract StandaloneCircusServer<T> createServer() throws Exception;

    @Before
    public void startServer() throws Throwable {
        setUpServerLoggerManager();
        this.server = createServer();
        this.server.start();
        prepareServer();
    }
    
    public void setUpServerLoggerManager() {
        this.serverLoggerManager = new SimpleLoggerManager( System.err, "server" );
        this.serverLoggerManager.setRootLevel( SERVER_ROOT_LEVEL );
    }
    
    public void prepareServer() throws Exception {
        
    }
    
    public T getServer() {
        return this.server.getServer();
    }

    @Before
    public void setUpClient() throws Exception {
        setUpClientLogger();
        InetSocketAddress address = new InetSocketAddress( "localhost", SimpleStompServer.DEFAULT_PORT );
        this.client = new AbstractStompClient( address );
        this.client.setLoggerManager( this.clientLoggerManager );
    }

    public void setUpClientLogger() {
        this.clientLoggerManager = new SimpleLoggerManager( System.err, "client" );
        this.clientLoggerManager.setRootLevel( CLIENT_ROOT_LEVEL );
    }

    @After
    public void stopServer() throws Throwable {
        this.server.stop();
    }
    
    @After 
    public void afterEverything() {
        
    }
    
    public MessageAccumulator accumulator(String name, boolean shouldAck, boolean shouldNack) {
        MessageAccumulator accumulator = this.accumulators.get( name );
        if (accumulator == null) {
            accumulator = new MessageAccumulator( shouldAck, shouldNack );
            this.accumulators.put( name, accumulator );
        }

        return accumulator;
    }

    public MessageAccumulator accumulator(String name) {
        return accumulator( name, false, false );
    }

}
