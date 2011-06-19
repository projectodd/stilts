package org.projectodd.stilts.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.StompMessage;

public class ClientMessageHandler extends AbstractClientHandler {

    public ClientMessageHandler(ClientContext clientContext) {
        super( clientContext );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        log.info( "reveived: " + e.getMessage() );
        if ( e.getMessage() instanceof StompMessage ) {
            handleStompMessage( channelContext, (StompMessage) e.getMessage() );
        } 
        super.messageReceived( channelContext, e );
    }

    protected void handleStompMessage(ChannelHandlerContext channelContext, StompMessage message) {
        if ( message.isError() ) {
            getClientContext().errorReceived( message );
        } else {
            getClientContext().messageReceived( message );
        }
    }


}
