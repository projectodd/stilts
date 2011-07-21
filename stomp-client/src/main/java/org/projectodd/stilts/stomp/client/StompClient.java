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

package org.projectodd.stilts.stomp.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.logging.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.VirtualExecutorService;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public class StompClient {

    public static final long DEFAULT_CONNECT_WAIT_TIME = 5000L;
    public static final long DEFAULT_DISCONNECT_WAIT_TIME = 5000L;

    private static Logger log = Logger.getLogger( StompClient.class );

    public static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }

    public StompClient(String uri) throws URISyntaxException {
        this( new URI( uri ) );
    }

    public StompClient(URI uri) throws URISyntaxException {
        String scheme = uri.getScheme();

        if (scheme.startsWith( "stomp" )) {
            int port = Constants.DEFAULT_PORT;
            String host = uri.getHost();
            int uriPort = uri.getPort();
            if (uriPort > 0) {
                port = uriPort;
            }

            this.serverAddress = new InetSocketAddress( host, port );

            if (scheme.endsWith( "+ws" )) {
                this.useWebSockets = true;
            }
        } else {

        }
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return this.executor;
    }

    public Version getVersion() {
        return this.version;
    }

    void setVersion(Version version) {
        this.version = version;
    }

    public boolean isConnected() {
        return (getConnectionState() == State.CONNECTED);
    }

    public boolean isDisconnected() {
        return (getConnectionState() == State.DISCONNECTED);
    }

    void setConnectionState(State connectionState) {
        synchronized (this.stateLock) {
            this.connectionState = connectionState;
            this.stateLock.notifyAll();
        }
    }

    void waitForConnected(long waitTime) throws InterruptedException, StompException {
        if (this.connectionState == State.CONNECTING) {
            synchronized (this.stateLock) {
                this.stateLock.wait( waitTime );
            }
        }
        if (this.connectionState != State.CONNECTED) {
            throw new StompException( "Connection timed out." );
        }
    }

    void waitForDisconnected(long waitTime) throws InterruptedException, StompException {
        if (this.connectionState == State.DISCONNECTING) {
            synchronized (this.stateLock) {
                this.stateLock.wait( waitTime );
            }
        }
        if (this.connectionState != State.DISCONNECTED) {
            throw new StompException( "Connection timed out." );
        }
    }

    public State getConnectionState() {
        synchronized (this.stateLock) {
            return this.connectionState;
        }
    }

    void messageReceived(StompMessage message) {
        boolean handled = false;
        String subscriptionId = message.getHeaders().get( Header.SUBSCRIPTION );
        if (subscriptionId != null) {
            ClientSubscription subscription = this.subscriptions.get( subscriptionId );
            if (subscription != null) {
                handled = subscription.messageReceived( message );
            }
        }
        if (!handled) {
            // TODO dispatch to global client handler.
        }
    }

    void errorReceived(StompMessage message) {
        log.error( message.getContentAsString() );
        String receiptId = message.getHeaders().get( Header.RECEIPT_ID );
        if (receiptId != null) {
            receiptReceived( receiptId, message );
        }

        synchronized (this.stateLock) {
            if (this.connectionState == State.CONNECTING) {
                this.connectionState = State.DISCONNECTED;
                this.stateLock.notifyAll();
            }
        }
    }

    public void connect() throws InterruptedException, StompException {
        connect( DEFAULT_CONNECT_WAIT_TIME );
    }

    public void connect(long waitTime) throws InterruptedException, StompException {

        if (this.executor == null) {
            this.executor = Executors.newFixedThreadPool( 4 );
        }

        ClientBootstrap bootstrap = new ClientBootstrap();
        ChannelPipelineFactory factory = createPipelineFactory();
        bootstrap.setPipelineFactory( factory );
        bootstrap.setFactory( createChannelFactory() );

        setConnectionState( State.CONNECTING );
        this.channel = bootstrap.connect( serverAddress ).await().getChannel();
        waitForConnected( waitTime );

        if (this.connectionState == State.CONNECTED) {
            log.info( "Connected" );
            if (this.clientListener != null) {
                this.clientListener.connected( this );
            }
        } else {
            // TODO: Handle error
            log.info( "Failed to connect" );
            disconnect();
        }
    }

    void connectWebSocket() {
    }

    String getNextTransactionId() {
        return "transaction-" + this.transactionCounter.getAndIncrement();
    }

    public void disconnect() throws InterruptedException, StompException {
        disconnect( DEFAULT_DISCONNECT_WAIT_TIME );
    }

    public void disconnect(long waitTime) throws InterruptedException, StompException {
        setConnectionState( State.DISCONNECTING );
        this.channel.close();
        waitForDisconnected( waitTime );
    }

    public SubscriptionBuilder subscribe(String destination) {
        return new SubscriptionBuilderImpl( this, destination );
    }

    public void send(StompMessage message) {
        StompFrame frame = StompFrames.newSendFrame( message );
        sendFrame( frame );
    }

    ClientSubscription subscribe(SubscriptionBuilderImpl builder) throws InterruptedException, ExecutionException {
        StompControlFrame frame = new StompControlFrame( Command.SUBSCRIBE, builder.getHeaders() );
        String subscriptionId = getNextSubscriptionId();
        frame.setHeader( Header.ID, subscriptionId );
        ReceiptFuture future = sendFrame( frame );
        future.await();
        if (future.isError()) {
            return null;
        } else {
            Executor executor = builder.getExecutor();
            if (executor == null) {
                executor = getExecutor();
            }
            ClientSubscription subscription = new ClientSubscription( this, subscriptionId, builder.getMessageHandler(), executor );
            this.subscriptions.put( subscription.getId(), subscription );
            return subscription;
        }
    }

    void unsubscribe(ClientSubscription subscription) throws InterruptedException, ExecutionException {
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

    void receiptReceived(String receiptId) {
        receiptReceived( receiptId, null );
    }

    void receiptReceived(String receiptId, StompMessage message) {
        ReceiptFuture future = this.receiptHandlers.remove( receiptId );

        if (future != null) {
            try {
                future.received( message );
            } catch (Exception e) {
                log.error( "Error during receipt of '" + receiptId + "'", e );
            }
        }
    }

    public ClientTransaction begin() throws StompException {
        String transactionId = getNextTransactionId();
        StompControlFrame frame = StompFrames.newBeginFrame( transactionId );
        ReceiptFuture future = sendFrame( frame );
        try {
            future.await();
            if (future.isError()) {
                return null;
            } else {
                ClientTransaction transaction = new ClientTransaction( this, transactionId );
                this.transactions.put( transaction.getId(), transaction );
                return transaction;
            }
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }

    public void abort(String transactionId) throws StompException {
        StompControlFrame frame = StompFrames.newAbortFrame( transactionId );
        ReceiptFuture future = sendFrame( frame );
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new StompException( e );
        } catch (ExecutionException e) {
            throw new StompException( e );
        }
    }

    public void commit(String transactionId) throws StompException {
        StompControlFrame frame = StompFrames.newCommitFrame( transactionId );
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
        return new StompClientPipelineFactory( this, new ClientContextImpl( this ), this.useWebSockets );
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

    private AtomicInteger receiptCounter = new AtomicInteger();
    private ConcurrentHashMap<String, ReceiptFuture> receiptHandlers = new ConcurrentHashMap<String, ReceiptFuture>( 20 );

    private AtomicInteger transactionCounter = new AtomicInteger();
    private Map<String, ClientTransaction> transactions = new HashMap<String, ClientTransaction>();

    private AtomicInteger subscriptionCounter = new AtomicInteger();
    private final Map<String, ClientSubscription> subscriptions = new HashMap<String, ClientSubscription>();

    private final Object stateLock = new Object();
    private State connectionState;

    private ClientListener clientListener;
    private Executor executor;
    private Channel channel;
    private InetSocketAddress serverAddress;
    private Version version = Version.VERSION_1_0;
    private boolean useWebSockets = false;

}
