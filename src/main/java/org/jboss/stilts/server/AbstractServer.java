package org.jboss.stilts.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;
import org.jboss.stilts.logging.Logger;
import org.jboss.stilts.logging.LoggerManager;
import org.jboss.stilts.logging.SimpleLoggerManager;
import org.jboss.stilts.protocol.StompPipelineFactory;
import org.jboss.stilts.spi.StompProvider;

public abstract class AbstractServer {

    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public AbstractServer(int port) {
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

    public abstract StompProvider getStompProvider() throws Exception;

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
     */
    public void stop() {
        this.channel.close();
        this.channel = null;
    }

    private int port;

    private LoggerManager loggerManager;
    private Logger log;
    private Executor executor;
    private Channel channel;

}
