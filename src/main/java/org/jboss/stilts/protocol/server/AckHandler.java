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
        String transactionId = frame.getHeader( Header.TRANSACTION );
        String subscriptionId = frame.getHeader( Header.SUBSCRIPTION );
        String messageId = frame.getHeader( Header.MESSAGE_ID );
        Acknowledger acknowledger = getContext().getAckManager().removeAcknowledger( transactionId, subscriptionId, messageId );
        if ( acknowledger != null ) {
            try {
                acknowledger.ack();
            } catch (Exception e) {
                sendError( channelContext, "Unable to ACK", frame );
            }
        }
    }

}
