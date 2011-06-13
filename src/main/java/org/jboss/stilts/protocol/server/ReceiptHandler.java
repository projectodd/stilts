package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.StompServer;

public class ReceiptHandler extends AbstractServerHandler {

    public ReceiptHandler(StompServer server, ConnectionContext context) {
        super( server, context );
    }

    public void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String receiptId = frame.getHeader( Header.RECEIPT_ID );
        if ( receiptId != null ) {
            StompFrame receipt = StompFrames.newReceiptFrame(receiptId);
            sendFrame( channelContext, receipt );
        }
    }
    
}
