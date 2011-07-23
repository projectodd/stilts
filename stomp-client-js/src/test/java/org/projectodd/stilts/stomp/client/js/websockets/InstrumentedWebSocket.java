package org.projectodd.stilts.stomp.client.js.websockets;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;
import org.jboss.netty.util.VirtualExecutorService;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.js.JSpec;

public class InstrumentedWebSocket {

    public enum ReadyState {
        CONNECTING,
        OPEN,
        CLOSING,
        CLOSED,
    }

    private Function onMessage;
    private Function onOpen;
    private Function onClose;
    private Function onError;
    private ExecutorService executor;
    private Channel channel;

    public InstrumentedWebSocket(String url) throws Exception {
        this( url, true );
    }

    public InstrumentedWebSocket(String url, boolean autoConnect) throws Exception {
        this.url = url;
        if (autoConnect) {
            connect();
        }
    }

    public void connect() throws InterruptedException, URISyntaxException, ExecutionException, TimeoutException {
        setReadyState( ReadyState.CONNECTING );
        ClientBootstrap bootstrap = new ClientBootstrap();

        this.executor = Executors.newFixedThreadPool( 4 );
        VirtualExecutorService bossExecutor = new VirtualExecutorService( this.executor );
        VirtualExecutorService workerExecutor = new VirtualExecutorService( this.executor );
        bootstrap.setFactory( new NioClientSocketChannelFactory( bossExecutor, workerExecutor ) );

        URI uri = new URI( this.url );

        String host = uri.getHost();
        int port = uri.getPort();
        if (port < 0) {
            port = Constants.DEFAULT_PORT;
        }

        ChannelPipelineFactory factory = new WebSocketClientPipelineFactory( this, host, port );
        bootstrap.setPipelineFactory( factory );

        InetSocketAddress addr = new InetSocketAddress( host, port );
        this.channel = bootstrap.connect( addr ).await().getChannel();
    }

    public int getReadyState() {
        return this.readyState.ordinal();
    }

    void setReadyState(ReadyState state) {
        synchronized (this.readyStateLock) {
            this.readyState = state;
            if (this.readyState == ReadyState.OPEN) {
                fireEvent( this.onOpen, "open" );
            } else if (this.readyState == ReadyState.CLOSED) {
                fireEvent( this.onClose, "close" );
            }
            this.readyStateLock.notifyAll();
        }
    }

    public void waitForClosedState() throws InterruptedException {
        synchronized (this.readyStateLock) {
            long fullWait = StompClient.DEFAULT_DISCONNECT_WAIT_TIME;
            long startTime = System.currentTimeMillis();
            long remainingWait = fullWait;

            System.err.println( "waiting for ws close" );
            while (remainingWait > 0 && this.readyState != ReadyState.CLOSED) {
                this.readyStateLock.wait( remainingWait );
                System.err.println( "wait notified on " + this.readyState );
                if (this.readyState != ReadyState.CLOSED) {
                    long elapsed = System.currentTimeMillis() - startTime;
                    remainingWait = fullWait - elapsed;
                }
            }
            System.err.println( "ws closed!" );
        }
    }

    public int getBufferedAmount() {
        return 0;
    }

    public void send(String message) {
        DefaultWebSocketFrame frame = new DefaultWebSocketFrame( message );
        this.channel.write( frame );
    }

    public void setOnopen(Function handler) {
        this.onOpen = handler;
        if (this.readyState == ReadyState.OPEN) {
            fireEvent( this.onOpen, "open" );
        }
    }

    protected void fireOnOpen() {
        fireEvent( this.onOpen, null );
    }

    public void setOnclose(Function handler) {
        this.onClose = handler;
    }

    protected void fireOnClose() {
        fireEvent( this.onClose, null );
    }

    public void setOnerror(Function handler) {
        this.onError = handler;
    }

    protected void fireOnError(Object error) {
        if (error instanceof Throwable) {
            JSpec.getCurrent().addError( (Throwable) error );
        }
        fireEvent( this.onError, null );
    }

    public void setOnmessage(Function handler) {
        this.onMessage = handler;
    }

    protected void fireOnMessage(Object message) {
        System.err.println( "sending message" );
        fireEvent( this.onMessage, new MessageEvent( message ) );
    }

    protected void fireEvent(Function handler, Object event) {
        if (handler != null) {
            Context context = Context.enter();
            Scriptable scope = handler.getParentScope();
            try {
                handler.call( context, scope, scope, new Object[] { event } );
            } finally {
                Context.exit();
            }
        }
    }

    public String getExtensions() {
        return "";
    }

    public String getProtocol() {
        return "";
    }

    public String getBinaryType() {
        return "blob";
    }

    public void close() {
        close( 0 );
    }

    public void close(int code) {
        setReadyState( ReadyState.CLOSING );
        this.channel.close();
    }

    private String url;
    private ReadyState readyState;
    private final Object readyStateLock = new Object();

}
