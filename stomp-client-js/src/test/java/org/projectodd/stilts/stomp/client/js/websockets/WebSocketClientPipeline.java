package org.projectodd.stilts.stomp.client.js.websockets;

import java.security.NoSuchAlgorithmException;

import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketConnectionNegotiator;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketHttpResponseDecoder;
import org.projectodd.stilts.stomp.protocol.DebugHandler;

public class WebSocketClientPipeline extends DefaultChannelPipeline {
    
    public WebSocketClientPipeline(TestableWebSocket socket, String host, int port) throws NoSuchAlgorithmException {
        addLast( "error-handler", new WebSocketClientErrorHandler( socket ) );
        addLast( "debug-HEAD", new DebugHandler( "CLIENT_HEAD"  ) );
        addLast( "http-encoder", new HttpRequestEncoder() );
        addLast( "http-decoder", new WebSocketHttpResponseDecoder() );
        addLast( "websocket-connection-negotiator", new WebSocketConnectionNegotiator( host, port ));
        this.waiter = new WebSocketClientConnectionWaiter( socket );
        addLast( "websocket-connection-waiter", this.waiter );
        addLast( "websocket-client-message-handler", new WebSocketClientMessageHandler( socket ) );
    }
    
    private WebSocketClientConnectionWaiter waiter;


}
