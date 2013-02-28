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

package org.projectodd.stilts.stomp.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.SSLContext;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.server.protocol.StompServerPipelineFactory;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompServer<T extends StompProvider> {

	public StompServer() throws UnknownHostException {
		this(null, Constants.DEFAULT_PORT, null);
	}
	
	public StompServer(SSLContext sslContext) throws UnknownHostException {
		this( null, (sslContext == null ? Constants.DEFAULT_PORT : Constants.DEFAULT_SECURE_PORT ), sslContext ) ;
	}

	/**
	 * Construct with a port.
	 * 
	 * @param port
	 *            The listen port to bind to.
	 * @throws UnknownHostException
	 */
	public StompServer(int port) {
		this(null, port, null);
	}
	
	public StompServer(int port, SSLContext sslContext) {
		this(null, port, sslContext );
	}

	public StompServer(InetAddress bindAddress, int port) {
		this( bindAddress, port, null );
	}
	
	public StompServer(InetAddress bindAddress, int port, SSLContext sslContext) {
		this.bindAddress = bindAddress;
		this.port = port;
		this.sslContext = sslContext;
	}
	
	public SSLContext getSSLContext() {
		return this.sslContext;
	}
	
	/**
	 * Retrieve the bind port.
	 * 
	 * @return The bind port.
	 */
	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public InetAddress getBindAddress() {
		return this.bindAddress;
	}

	public void setBindAddress(InetAddress bindAddress) {
		this.bindAddress = bindAddress;
	}

	public void setChannelExecutor(Executor executor) {
		this.channelExecutor = executor;
	}

	public Executor getChannelExecutor() {
		return this.channelExecutor;
	}

	public void setMessageHandlingExecutor(Executor executor) {
		this.messageHandlingExecutor = executor;
	}

	public Executor getMessageHandlingExector() {
		return this.messageHandlingExecutor;
	}

	public void setStompProvider(T stompProvider) {
		this.stompProvider = stompProvider;
	}

	public T getStompProvider() {
		return this.stompProvider;
	}

	/**
	 * Start this server.
	 * 
	 * @throws Throwable
	 * 
	 */
	public void start() throws Exception {

		if (this.channelExecutor == null) {
			this.channelExecutor = Executors.newCachedThreadPool( new ThreadFactory() {
			    private int counter = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName( "stomp-server-" + ( ++counter ) );
                    thread.setDaemon( true );
                    return thread;
                }
            });
			this.destroyExecutor = true;
		}

		if (this.channelPipelineFactory == null) {
			this.channelPipelineFactory = new StompServerPipelineFactory( getStompProvider(), getMessageHandlingExector(), getSSLContext() );
		}

		ServerBootstrap bootstrap = createServerBootstrap();
		InetSocketAddress socketAddr = null;

		if (this.bindAddress == null) {
			socketAddr = new InetSocketAddress(this.port);
		} else {
			socketAddr = new InetSocketAddress(this.bindAddress, this.port);
		}

		this.channel = bootstrap.bind( socketAddr );
	}

	protected ServerBootstrap createServerBootstrap() throws Exception {
		ServerBootstrap bootstrap = new ServerBootstrap(createChannelFactory());
		bootstrap.setOption("reuseAddress", true);

		bootstrap.setPipelineFactory(getChannelPipelineFactory());
		return bootstrap;
	}

	protected void setChannelPipelineFactory(
			ChannelPipelineFactory channelPipelineFactory) {
		this.channelPipelineFactory = channelPipelineFactory;
	}

	protected ChannelPipelineFactory getChannelPipelineFactory() {
		return this.channelPipelineFactory;
	}

	protected ServerSocketChannelFactory createChannelFactory() {
		VirtualExecutorService bossExecutor = new VirtualExecutorService(
				this.channelExecutor);
		VirtualExecutorService workerExecutor = new VirtualExecutorService(
				this.channelExecutor);
		return new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
	}

	/**
	 * Stop this server.
	 * 
	 * @throws Exception
	 * @throws Throwable
	 */
	public void stop() throws Exception {
		if ( this.destroyExecutor ) {
		    if ( this.channelExecutor instanceof ExecutorService ) {
		        ((ExecutorService) this.channelExecutor).shutdown();
		        this.channelExecutor = null;
		        this.destroyExecutor = false;
		    }
		}
		this.channel.close();
		this.channel = null;
	}

	private SSLContext sslContext;
	private int port;
	private InetAddress bindAddress;

	private T stompProvider;
	private Executor channelExecutor;
	private boolean destroyExecutor;
	private ChannelPipelineFactory channelPipelineFactory;
	private Executor messageHandlingExecutor;
	private Channel channel;

}
