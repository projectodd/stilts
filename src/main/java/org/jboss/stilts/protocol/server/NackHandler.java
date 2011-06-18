package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.StompProvider;

public class NackHandler extends AbstractControlFrameHandler {

    public NackHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.NACK );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        String messageId = frame.getHeader( Header.MESSAGE_ID );
        Acknowledger acknowledger = getContext().getAckManager().removeAcknowledger( messageId );
        String transactionId = frame.getHeader( Header.TRANSACTION );
        System.err.println( "--------" );
        System.err.println( "A: " + acknowledger );
        System.err.println( "M: " + messageId );
        System.err.println( "T: " + transactionId );
        System.err.println( "--------" );
        if ( acknowledger != null ) {
            try {
                getClientAgent().nack( acknowledger, transactionId );
            } catch (Exception e) {
                sendError( channelContext, "Unable to NACK", frame );
            }
        }
    }

}
