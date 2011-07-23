package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class WebSocketClientErrorHandler extends SimpleChannelUpstreamHandler {

    public WebSocketClientErrorHandler(InstrumentedWebSocket socket) {
        this.socket = socket;
    }

    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        this.socket.fireOnError( e.getCause() );
        super.exceptionCaught( ctx, e );
    }


    private InstrumentedWebSocket socket;

}
