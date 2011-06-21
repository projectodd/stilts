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

package org.projectodd.stilts.stomp.client;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.logging.SimpleLoggerManager;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class AbstractStompClient implements StompClient {

    public AbstractStompClient(SocketAddress serverAddress) {
        this.serverAddress = serverAddress;
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

    public boolean isConnected() {
        return (getConnectionState() == State.CONNECTED);
    }

    public boolean isDisconnected() {
        return (getConnectionState() == State.DISCONNECTED);
    }

    void setConnectionState(State connectionState) {
        synchronized (this.stateLock) {
            log.info( "Set state: " + connectionState );
            this.connectionState = connectionState;
            this.stateLock.notifyAll();
        }
    }

    void waitForConnected() throws InterruptedException {
        synchronized (this.stateLock) {
            while (this.connectionState == State.CONNECTING) {
                this.stateLock.wait();
            }
        }
    }

    void waitForDisconnected() throws InterruptedException {
        synchronized (this.stateLock) {
            while (this.connectionState == State.DISCONNECTING) {
                this.stateLock.wait();
            }
        }
    }

    public State getConnectionState() {
        synchronized (this.stateLock) {
            return this.connectionState;
        }
    }

    void messageReceived(StompMessage message) {
        log.info( "received message: " + message );
        boolean handled = false;
        String subscriptionId = message.getHeaders().get( Header.SUBSCRIPTION );
        if (subscriptionId != null) {
            DefaultClientSubscription subscription = this.subscriptions.get( subscriptionId );
            if (subscription != null) {
                MessageHandler handler = subscription.getMessageHandler();
                if (handler != null) {
                    handler.handle( message );
                    handled = true;
                }
            }
        }
        if (!handled) {
            // TODO dispatch to global client handler.
        }
    }

    void errorReceived(StompMessage message) {
        log.info( "received error: " + message );
        String receiptId = message.getHeaders().get( Header.RECEIPT_ID );
        if (receiptId != null) {
            receiptReceived( receiptId, message );
        }
    }

    public void connect() throws InterruptedException {

        if (this.loggerManager == null) {
            this.loggerManager = SimpleLoggerManager.DEFAULT_INSTANCE;
        }

        this.log = this.loggerManager.getLogger( "client" );

        if (this.executor == null) {
            this.executor = Executors.newFixedThreadPool( 2 );
        }

        ClientBootstrap bootstrap = new ClientBootstrap();
        ChannelPipelineFactory factory = createPipelineFactory();
        bootstrap.setPipelineFactory( factory );
        bootstrap.setFactory( createChannelFactory() );

        connectInternal( bootstrap );
    }

    void connectInternal(ClientBootstrap bootstrap) throws InterruptedException {

        log.info( "Connecting" );

        setConnectionState( State.CONNECTING );

        this.channel = bootstrap.connect( serverAddress ).await().getChannel();
        StompControlFrame frame = new StompControlFrame( Command.CONNECT );
        sendFrame( frame );
        waitForConnected();

        if (this.connectionState == State.CONNECTED) {
            log.info( "Connected" );
            if (this.clientListener != null) {
                this.clientListener.connected( this );
            }
        } else {
            log.info( "Failed to connect" );
        }
    }

    String getNextTransactionId() {
        return "transaction-" + this.transactionCounter.getAndIncrement();
    }

    public void disconnect() throws InterruptedException {
        StompControlFrame frame = new StompControlFrame( Command.DISCONNECT );
        setConnectionState( State.DISCONNECTING );
        sendFrame( frame, new Callable<Void>() {
            public Void call() throws Exception {
                setConnectionState( State.DISCONNECTED );
                return null;
            }
        } );

        waitForDisconnected();
    }

    public SubscriptionBuilder subscribe(String destination) {
        return new DefaultSubscriptionBuilder( this, destination );
    }

    public void send(StompMessage message) {
        log.debug( "Sending outbound message: " + message );
        StompFrame frame = StompFrames.newSendFrame( message );
        sendFrame( frame );
    }

    DefaultClientSubscription subscribe(DefaultSubscriptionBuilder builder) throws InterruptedException, ExecutionException {
        StompControlFrame frame = new StompControlFrame( Command.SUBSCRIBE, builder.getHeaders() );
        String subscriptionId = getNextSubscriptionId();
        frame.setHeader( Header.ID, subscriptionId );
        ReceiptFuture future = sendFrame( frame );
        future.await();
        if (future.isError()) {
            return null;
        } else {
            DefaultClientSubscription subscription = new DefaultClientSubscription( this, subscriptionId, builder.getMessageHandler() );
            this.subscriptions.put( subscription.getId(), subscription );
            return subscription;
        }
    }

    void unsubscribe(DefaultClientSubscription subscription) throws InterruptedException, ExecutionException {
        StompControlFrame frame = new StompControlFrame( Command.UNSUBSCRIBE );
        frame.setHeader( Header.ID, subscription.getId() );
        sendFrame( frame ).await();
        subscription.setActive( false );
    }

    String getNextSubscriptionId() {
        return "subscription-" + this.subscriptionCounter.getAndIncrement();
    }

    ReceiptFuture sendFrame(StompFrame frame) {
        return sendFrame( frame, NOOP );
    }

    ReceiptFuture sendFrame(StompFrame frame, Callable<Void> receiptHandler) {
        ReceiptFuture future = null;
        String receiptId = getNextReceiptId();
        frame.setHeader( Header.RECEIPT, receiptId );
        future = new ReceiptFuture( receiptHandler );
        this.receiptHandlers.put( receiptId, future );
        this.channel.write( frame );
        return future;
    }

    String getNextReceiptId() {
        return "receipt-" + this.receiptCounter.getAndIncrement();
    }

    public void receiptReceived(String receiptId) {
        receiptReceived( receiptId, null );
    }

    public void receiptReceived(String receiptId, StompMessage message) {
        ReceiptFuture future = this.receiptHandlers.remove( receiptId );

        if (future != null) {
            try {
                future.received( message );
            } catch (Exception e) {
                log.error( "Error during receipt of '" + receiptId + "'", e );
            }
        }
    }

    @Override
    public ClientTransaction begin() throws StompException {
        String transactionId = getNextTransactionId();
        StompControlFrame frame = new StompControlFrame( Command.BEGIN );
        frame.setHeader( Header.TRANSACTION, transactionId );
        ReceiptFuture future = sendFrame( frame );
        try {
            future.await();
            if (future.isError()) {
                return null;
            } else {
                DefaultClientTransaction transaction = new DefaultClientTransaction( this, transactionId );
                this.transactions.put( transaction.getId(), transaction );
                return transaction;
            }
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }
    
    
    @Override
    public void abort(String transactionId) throws StompException {
        StompControlFrame frame = new StompControlFrame( Command.ABORT );
        frame.setHeader( Header.TRANSACTION, transactionId );
        ReceiptFuture future = sendFrame( frame );
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }
    
    @Override
    public void commit(String transactionId) throws StompException {
        StompControlFrame frame = new StompControlFrame( Command.COMMIT );
        frame.setHeader( Header.TRANSACTION, transactionId );
        ReceiptFuture future = sendFrame( frame );
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }

    protected ChannelPipelineFactory createPipelineFactory() {
        return new StompClientPipelineFactory( this, new DefaultClientContext( this ) );
    }

    protected ClientSocketChannelFactory createChannelFactory() {
        VirtualExecutorService bossExecutor = new VirtualExecutorService( this.executor );
        VirtualExecutorService workerExecutor = new VirtualExecutorService( this.executor );
        return new NioClientSocketChannelFactory( bossExecutor, workerExecutor );
    }

    public void close() throws InterruptedException {
        this.channel.getCloseFuture().await();
    }

    private static final Callable<Void> NOOP = new Callable<Void>() {
        public Void call() throws Exception {
            return null;
        }
    };

    private Logger log;

    private AtomicInteger receiptCounter = new AtomicInteger();
    private ConcurrentHashMap<String, ReceiptFuture> receiptHandlers = new ConcurrentHashMap<String, ReceiptFuture>( 20 );

    private AtomicInteger transactionCounter = new AtomicInteger();
    private Map<String, DefaultClientTransaction> transactions = new HashMap<String, DefaultClientTransaction>();

    private AtomicInteger subscriptionCounter = new AtomicInteger();
    private final Map<String, DefaultClientSubscription> subscriptions = new HashMap<String, DefaultClientSubscription>();

    private final Object stateLock = new Object();
    private State connectionState;

    private ClientListener clientListener;
    private LoggerManager loggerManager;
    private Executor executor;
    private Channel channel;
    private SocketAddress serverAddress;

}
