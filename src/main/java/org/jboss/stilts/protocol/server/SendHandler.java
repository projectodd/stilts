package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.StompServer;

public class SendHandler extends AbstractServerHandler {

    public SendHandler(StompServer server, ConnectionContext context) {
        super( server, context );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompMessage) {
            getContext().getClientAgent().send( (StompMessage) e.getMessage()  );
        }
        super.messageReceived( ctx, e );
    }

}
