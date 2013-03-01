package org.projectodd.stilts.stomp.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;

public abstract class AbstractConnector implements Connector {

    public AbstractConnector(InetSocketAddress bindAddress) {
        this.bindAddress = bindAddress;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }

    public InetSocketAddress getBindAddress() {
        return this.bindAddress;
    }

    protected abstract ChannelPipelineFactory getChannelPipelineFactory();

    protected ServerBootstrap createServerBootstrap() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap( createChannelFactory() );
        bootstrap.setOption( "reuseAddress", true );
        bootstrap.setPipelineFactory( getChannelPipelineFactory() );
        return bootstrap;
    }

    protected ServerSocketChannelFactory createChannelFactory() {
        this.channelExecutor = Executors.newCachedThreadPool( new ThreadFactory() {
            private int counter = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon( true );
                thread.setName( "stomp-" + getClass().getSimpleName() + "-" + (++counter)  );
                return thread;
            }
        } );
        VirtualExecutorService bossExecutor = new VirtualExecutorService( this.channelExecutor );
        VirtualExecutorService workerExecutor = new VirtualExecutorService( this.channelExecutor );
        return new NioServerSocketChannelFactory( bossExecutor, workerExecutor );
    }

    public synchronized void start() throws Exception {
        if ( this.running ) {
            return;
        }
        this.bootstrap = createServerBootstrap();
        this.channel = bootstrap.bind( getBindAddress() );
        this.running = true;
    }

    public synchronized void stop() throws Exception {
        if (  ! this.running ) {
            return;
        }
        
        ChannelFuture future = this.channel.close();
        future.addListener( new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                AbstractConnector.this.bootstrap.shutdown();
                AbstractConnector.this.bootstrap.releaseExternalResources();
            }
        } );
        future.await();
        this.channelExecutor.shutdown();
        this.channelExecutor = null;
        this.channel = null;
    }

    private boolean running;
    private ServerBootstrap bootstrap;
    private Channel channel;
    private InetSocketAddress bindAddress;
    private Server server;
    private ExecutorService channelExecutor;

}
