package org.projectodd.stilts.stomp.client.js.websockets;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketConnectionNegotiator;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketHttpResponseDecoder;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.protocol.websocket.ietf17.Ietf17Handshake;

public class WebSocketClientPipeline extends DefaultChannelPipeline {
    
    public WebSocketClientPipeline(InstrumentedWebSocket socket, String host, int port) throws NoSuchAlgorithmException {
        //Ietf00Handshake handshake = new Ietf00Handshake();
        //Ietf07Handshake handshake = new Ietf07Handshake();
        Ietf17Handshake handshake = new Ietf17Handshake();
        addLast( "error-handler", new WebSocketClientErrorHandler( socket ) );
        addLast( "debug-HEAD", new DebugHandler( "CLIENT_HEAD"  ) );
        addLast( "http-encoder", new HttpRequestEncoder() );
        addLast( "http-decoder", new WebSocketHttpResponseDecoder( handshake ) );
        addLast( "websocket-connection-negotiator", new WebSocketConnectionNegotiator( new InetSocketAddress( host, port ) , handshake, false  ));
        this.waiter = new WebSocketClientConnectionWaiter( socket );
        addLast( "websocket-connection-waiter", this.waiter );
        addLast( "websocket-client-message-handler", new WebSocketClientMessageHandler( socket ) );
    }
    
    private WebSocketClientConnectionWaiter waiter;


}
