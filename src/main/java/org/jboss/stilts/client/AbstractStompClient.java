package org.jboss.stilts.client;

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
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.logging.Logger;
import org.jboss.stilts.logging.LoggerManager;
import org.jboss.stilts.logging.SimpleLoggerManager;
import org.jboss.stilts.protocol.StompControlFrame;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;

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
        this.globalTransaction.messageReceived( message );
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
            this.globalTransaction = new DefaultClientTransaction( this, getNextTransactionId(), true );
            if (this.clientListener != null) {
                this.clientListener.connected( this );
            }
        } else {
            log.info( "Failed to connect" );
        }
    }

    String getNextTransactionId() {
        return "" + this.transactionCounter.getAndIncrement();
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
        return new DefaultSubscriptionBuilder( this.globalTransaction, destination );
    }

    public void send(StompMessage message) {
        log.debug( "Sending outbound message: " + message );
        this.channel.write( message );
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
            return new DefaultClientSubscription( builder.getClientTransaction(), subscriptionId, builder.getMessageHandler() );
        }
    }

    void unsubscribe(DefaultClientSubscription subscription) throws InterruptedException, ExecutionException {
        StompControlFrame frame = new StompControlFrame( Command.UNSUBSCRIBE );
        frame.setHeader( Header.ID, subscription.getId() );
        sendFrame( frame ).await();
        subscription.setActive( false );
    }

    String getNextSubscriptionId() {
        return "" + this.subscriptionCounter.getAndIncrement();
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
        return "" + this.receiptCounter.getAndIncrement();
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

    protected ChannelPipelineFactory createPipelineFactory() {
        return new StompClientPipelineFactory( new DefaultClientContext( this ) );
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
    private DefaultClientTransaction globalTransaction;
    private Map<String, DefaultClientTransaction> transactions = new HashMap<String, DefaultClientTransaction>();

    private AtomicInteger subscriptionCounter = new AtomicInteger();

    private final Object stateLock = new Object();
    private State connectionState;

    private ClientListener clientListener;
    private LoggerManager loggerManager;
    private Executor executor;
    private Channel channel;
    private SocketAddress serverAddress;

}
