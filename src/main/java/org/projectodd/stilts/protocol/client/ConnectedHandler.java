package org.projectodd.stilts.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.client.StompClient.State;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;

public class ConnectedHandler extends AbstractClientControlFrameHandler {

    public ConnectedHandler(ClientContext clientContext) {
        super( clientContext, Command.CONNECTED );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info(  "Received: " + frame  );
        getClientContext().setConnectionState( State.CONNECTED );
    }

}
