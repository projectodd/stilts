package org.projectodd.stilts.stomp.protocol.websocket;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamChannelStateEvent;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketFrame.FrameType;

public class WebSocketDisconnectionNegotiator implements ChannelDownstreamHandler, ChannelUpstreamHandler {

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (this.closeRequest != null) {
            if (e instanceof MessageEvent) {
                Object message = ((MessageEvent) e).getMessage();
                if (message instanceof WebSocketFrame) {
                    WebSocketFrame frame = (WebSocketFrame) message;

                    if (frame.getType() == FrameType.CLOSE) {
                        ctx.sendDownstream( this.closeRequest );
                        return;
                    }
                }
            }
        }

        ctx.sendUpstream( e );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            ChannelState state = ((ChannelStateEvent) e).getState();
            if (state == ChannelState.OPEN && Boolean.FALSE.equals( ((ChannelStateEvent) e).getValue() )) {
                closeRequested( ctx, (ChannelStateEvent) e );
                return;
            }
        }

        ctx.sendDownstream( e );
    }

    public void closeRequested(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.closeRequest = e;
        DefaultWebSocketFrame closeFrame = new DefaultWebSocketFrame( FrameType.CLOSE );
        Channels.write( ctx.getChannel(), closeFrame );
    }

    private ChannelStateEvent closeRequest;
}
