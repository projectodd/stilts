package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.StompProvider;

public class SendHandler extends AbstractProviderHandler {

    public SendHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log.info( "SEND: " + e.getMessage() );
        if (e.getMessage() instanceof StompMessage) {
            log.info( "SEND: " + e.getMessage() + " via " + getContext()  );
            log.info( "SEND: " + e.getMessage() + " via " + getContext().getStompConnection()  );
            StompMessage message = (StompMessage) e.getMessage();
            String transactionId = message.getHeaders().get( Header.TRANSACTION );
            getContext().getStompConnection().send( (StompMessage) e.getMessage(), transactionId );
        }
        super.messageReceived( ctx, e );
    }

}
