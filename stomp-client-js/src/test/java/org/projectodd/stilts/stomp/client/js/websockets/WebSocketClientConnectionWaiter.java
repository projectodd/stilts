package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.projectodd.stilts.stomp.client.js.websockets.InstrumentedWebSocket.ReadyState;

public class WebSocketClientConnectionWaiter implements ChannelUpstreamHandler {

    public WebSocketClientConnectionWaiter(InstrumentedWebSocket socket) {
        this.socket = socket;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log.debug( "UPSTREAM: "+ e );
        if ( e instanceof ChannelStateEvent ) {
            ChannelStateEvent event = (ChannelStateEvent) e;
            if ( event.getState() == ChannelState.CONNECTED && event.getValue() != null ) {
                ctx.getPipeline().replace( this, "websockets-client-disconnection-waiter", new WebSocketClientDisconnectionWaiter( this.socket ) );
                this.socket.setReadyState( ReadyState.OPEN );
            }
        } else if ( e instanceof ExceptionEvent ) {
            ExceptionEvent event = (ExceptionEvent) e;
            event.getCause().printStackTrace();
        } else {
            ctx.sendUpstream( e );
        }
    }
    
    private InstrumentedWebSocket socket;
    private static Logger log = Logger.getLogger(WebSocketClientConnectionWaiter.class);

}
