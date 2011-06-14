package org.jboss.stilts.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;

public class ClientReceiptHandler extends AbstractClientControlFrameHandler {

    public ClientReceiptHandler(ClientContext clientContext) {
        super( clientContext, Command.RECEIPT );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String receiptId = frame.getHeader( Header.RECEIPT_ID );
        getClientContext().receiptReceived( receiptId );
    }

}
