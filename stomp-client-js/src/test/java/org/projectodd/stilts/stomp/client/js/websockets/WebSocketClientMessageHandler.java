package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;

public class WebSocketClientMessageHandler extends SimpleChannelUpstreamHandler {

    public WebSocketClientMessageHandler(WebSocket socket) {
        this.socket = socket;
    }
    
    

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        System.err.println( "UPSTREAM: " + e.getClass() );
        super.handleUpstream( ctx, e );
    }



    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        System.err.println( "messassssssssss" + e.getMessage() );
        if (e.getMessage() instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) e.getMessage();
            this.socket.fireOnMessage( frame.getTextData() );

        } else {
            super.messageReceived( ctx, e );
        }
    }

    private WebSocket socket;

}
