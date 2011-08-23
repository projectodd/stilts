package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketFrame;

public class WebSocketClientMessageHandler extends SimpleChannelUpstreamHandler {

    public WebSocketClientMessageHandler(InstrumentedWebSocket socket) {
        this.socket = socket;
    }
    
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) e.getMessage();
            this.socket.fireOnMessage( frame.getTextData() );

        } else {
            super.messageReceived( ctx, e );
        }
    }

    private InstrumentedWebSocket socket;

}
