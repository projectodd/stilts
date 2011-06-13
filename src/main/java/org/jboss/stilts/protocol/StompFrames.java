package org.jboss.stilts.protocol;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;

public class StompFrames {
    
    public static StompFrame newConnectedFrame(String sessionId) {
        StompControlFrame frame = new StompControlFrame( Command.CONNECTED );
        frame.setHeader( Header.SESSION, sessionId );
        return frame;
    }
    
    public static StompFrame newErrorFrame(String message) {
        StompContentFrame frame = new StompContentFrame( Command.ERROR );
        frame.setContent( ChannelBuffers.copiedBuffer( message.getBytes() ) );
        return frame;
    }

    public static StompFrame newReceiptFrame(String receiptId) {
        StompControlFrame receipt = new StompControlFrame( Command.RECEIPT );
        receipt.setHeader( Header.RECEIPT_ID, receiptId );
        return receipt;
    }

}
