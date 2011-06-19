package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.NotConnectedException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.StompProvider;

public class DisconnectHandler extends AbstractControlFrameHandler {

    public DisconnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.DISCONNECT );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            getStompConnection().disconnect();
        } catch (NotConnectedException e) {
            // ignore, we're shutting down anyhow
        }
        getContext().setActive( false );
        String receiptId = frame.getHeader( Header.RECEIPT );
        if (receiptId != null) {
            ChannelFuture future = channelContext.getChannel().write( StompFrames.newReceiptFrame( receiptId ) );
            future.addListener( ChannelFutureListener.CLOSE );
        } else {
            channelContext.getChannel().close();
        }
    }

}
