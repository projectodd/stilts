package org.projectodd.stilts.stomp.client.protocol;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public class StompDisconnectionNegotiator implements ChannelDownstreamHandler, ChannelUpstreamHandler {
    
    private static Logger log = Logger.getLogger(StompDisconnectionNegotiator.class);
    
    public StompDisconnectionNegotiator(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (this.closeRequest != null) {
            if (e instanceof MessageEvent) {
                Object message = ((MessageEvent) e).getMessage();
                if (message instanceof StompControlFrame) {
                    StompControlFrame frame = (StompControlFrame) message;

                    if ( frame.getCommand() == Command.RECEIPT && frame.getHeader( Header.RECEIPT_ID ).equals( this.receiptId ) ) {
                        ctx.sendDownstream( this.closeRequest );
                        this.clientContext.setConnectionState( State.DISCONNECTED );
                        return;
                    }
                }
            }
        }

        ctx.sendUpstream( e );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            if (!Boolean.TRUE.equals( ((ChannelStateEvent) e).getValue() )) {
                closeRequested( ctx, (ChannelStateEvent) e );
                return;
            }
        }

        ctx.sendDownstream( e );
    }

    public void closeRequested(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.closeRequest = e;
        
        StompFrame frame = StompFrames.newDisconnectFrame();
        this.receiptId = frame.getHeader( Header.RECEIPT );
        log.tracef("send: %s", frame);
        Channels.write( ctx.getChannel(), frame );
    }

    private ClientContext clientContext;
    private String receiptId;
    private ChannelStateEvent closeRequest;
}
