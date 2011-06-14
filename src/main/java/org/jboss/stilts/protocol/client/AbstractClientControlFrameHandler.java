package org.jboss.stilts.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;

public abstract class AbstractClientControlFrameHandler extends AbstractClientHandler {

    public AbstractClientControlFrameHandler(ClientContext clientContext, Command command) {
        super( clientContext );
        this.command = command;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        log.trace(  "received: " + e.getMessage() );
        if ( e.getMessage() instanceof StompFrame ) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        } 
        super.messageReceived( channelContext, e );
    }

    protected void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        if ( frame.getCommand().equals( this.command ) ) {
            handleControlFrame( channelContext, frame );
        }
    }
    
    protected abstract void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame);
    
    private Command command;


}
