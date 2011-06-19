package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.StompProvider;

public class AckHandler extends AbstractControlFrameHandler {

    public AckHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.ACK );
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
                getStompConnection().ack( acknowledger, transactionId );
            } catch (Exception e) {
                sendError( channelContext, "Unable to ACK", frame );
            }
        }
    }

}
