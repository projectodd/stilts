package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.StompProvider;

public class ReceiptHandler extends AbstractProviderHandler {

    public ReceiptHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompFrame) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        }
        super.messageReceived( channelContext, e );
    }

    public void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info(  "Checking receipt for: " + frame  );
        if ( ! getContext().isActive() ) {
            log.info( "Connection not active, no ACK required" );
            return;
        }
        String receiptId = frame.getHeader( Header.RECEIPT );
        if ( receiptId != null ) {
            StompFrame receipt = StompFrames.newReceiptFrame(receiptId);
            sendFrame( channelContext, receipt );
        }
    }
    
}
