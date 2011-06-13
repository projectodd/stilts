package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.spi.StompServer;

public class DisconnectHandler extends AbstractControlFrameHandler {

    public DisconnectHandler(StompServer server, ConnectionContext context) {
        super( server, context, Command.DISCONNECT );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            getClientAgent().disconnect();
        } catch (NotConnectedException e) {
            // ignore, we're shutting down anyhow
        }
        channelContext.getChannel().close();
    }
    
}
