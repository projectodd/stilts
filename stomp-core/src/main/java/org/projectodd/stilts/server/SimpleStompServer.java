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

package org.projectodd.stilts.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.transaction.TransactionManager;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;
import org.projectodd.stilts.helpers.DefaultServerEnvironment;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.logging.SimpleLoggerManager;
import org.projectodd.stilts.stomp.protocol.StompPipelineFactory;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomp.spi.StompServerEnvironment;

public class SimpleStompServer<T extends StompProvider> {
    
    public static final int DEFAULT_PORT = 8675;

    public SimpleStompServer() {
        this( DEFAULT_PORT );
    }
    
    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public SimpleStompServer(int port) {
        this.port = port;
    }

    /**
     * Retrieve the bind port.
     * 
     * @return The bind port.
     */
    public int getPort() {
        return this.port;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return this.executor;
    }
    
    public void setLoggerManager(LoggerManager loggerManager) {
        this.loggerManager = loggerManager;
    }
    
    public LoggerManager getLoggerManager() {
        return this.loggerManager;
    }
    
    public void setStompProvider(T stompProvider) {
        this.stompProvider = stompProvider;
    }
    
    public  T getStompProvider() throws Exception {
        return this.stompProvider;
    }

    protected StompServerEnvironment getServerEnvironment() {
        DefaultServerEnvironment env = new DefaultServerEnvironment();
        env.setTransactionManager( this.transactionManager );
        return env;
    }
    
    /**
     * Start this server.
     * @throws Throwable 
     * 
     */
    public void start() throws Throwable {
        if ( this.loggerManager == null ) {
            this.loggerManager = SimpleLoggerManager.DEFAULT_INSTANCE;
        }
        
        this.log = this.loggerManager.getLogger( "server" );
        
        if (this.executor == null) {
            this.executor = Executors.newFixedThreadPool( 2 );
        }
        
        ServerBootstrap bootstrap = createServerBootstrap();
        this.channel = bootstrap.bind( new InetSocketAddress( this.port ) );
    }

    protected ServerBootstrap createServerBootstrap() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap( createChannelFactory() );
        bootstrap.setOption( "reuseAddress", true );
        
        bootstrap.setPipelineFactory( new StompPipelineFactory( getStompProvider(), this.loggerManager ) );
        return bootstrap;
    }
    
    protected ServerSocketChannelFactory createChannelFactory() {
        VirtualExecutorService bossExecutor = new VirtualExecutorService( this.executor );
        VirtualExecutorService workerExecutor = new VirtualExecutorService( this.executor );
        return new NioServerSocketChannelFactory( bossExecutor, workerExecutor );
    }

    /**
     * Stop this server.
     * @throws Exception 
     * @throws Throwable 
     */
    public void stop() throws Throwable {
        this.channel.close();
        this.channel = null;
    }

    private int port;

    private T stompProvider;
    private TransactionManager transactionManager;
    private LoggerManager loggerManager;
    private Logger log;
    private Executor executor;
    private Channel channel;

}
