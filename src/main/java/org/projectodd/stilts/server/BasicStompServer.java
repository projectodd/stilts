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
import org.projectodd.stilts.protocol.StompPipelineFactory;
import org.projectodd.stilts.spi.StompProvider;
import org.projectodd.stilts.spi.StompServerEnvironment;

public class BasicStompServer {
    
    public static final int DEFAULT_PORT = 8675;

    public BasicStompServer() {
        this( DEFAULT_PORT );
    }
    
    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public BasicStompServer(int port) {
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
    
    public void setStompProvider(StompProvider stompProvider) {
        this.stompProvider = stompProvider;
    }
    
    public  StompProvider getStompProvider() throws Exception {
        return this.stompProvider;
    }

    protected StompServerEnvironment getServerEnvironment() {
        DefaultServerEnvironment env = new DefaultServerEnvironment();
        env.setTransactionManager( this.transactionManager );
        return env;
    }
    
    /**
     * Start this server.
     * 
     */
    public void start() throws Exception {
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
     */
    public void stop() throws Exception {
        this.channel.close();
        this.channel = null;
    }

    private int port;

    private StompProvider stompProvider;
    private TransactionManager transactionManager;
    private LoggerManager loggerManager;
    private Logger log;
    private Executor executor;
    private Channel channel;

}
