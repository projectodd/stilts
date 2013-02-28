package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.stomp.protocol.websocket.DefaultWebSocketFrame;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketFrame;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketFrame.FrameType;

public class PingHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) e.getMessage();

            if (frame.getType() == FrameType.PING) {
                if (ctx.getChannel().isWritable()) {
                    ctx.getChannel().write( new DefaultWebSocketFrame( FrameType.PONG, frame.getBinaryData() ) );
                    return;
                }
            }

        }
        super.messageReceived( ctx, e );
    }

}
