package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.StompServer;

public abstract class AbstractControlFrameHandler extends AbstractServerHandler {

    public AbstractControlFrameHandler(StompServer server, ConnectionContext context, Command command) {
        super( server, context );
        this.command = command;
    }
    
    public StompServer getServer() {
        return this.server;
    }
    
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public ClientAgent getClientAgent() {
        return this.context.getClientAgent();
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if ( e.getMessage() instanceof StompFrame ) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        } 
        super.messageReceived( channelContext, e );
    }

    protected void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        if ( this.requiresClientIdentification && getContext().getClientAgent() == null ) {
            sendErrorAndClose( channelContext, "Must CONNECT first" );
            return;
        }
        
        if ( frame.getCommand().equals( this.command ) ) {
            handleControlFrame( channelContext, frame );
        }
    }
    
    protected ChannelFuture sendFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        return channelContext.getChannel().write(  frame  );
    }
    
    protected ChannelFuture sendError(ChannelHandlerContext channelContext, String message) {
        return sendFrame( channelContext, StompFrames.newErrorFrame( message ) );
    }
    
    protected void sendErrorAndClose(ChannelHandlerContext channelContext, String message) {
        ChannelFuture future = sendError( channelContext, message );
        future.addListener( ChannelFutureListener.CLOSE );
    }
    
    protected void setRequiresClientIdentification(boolean requiresClientIdentification) {
        this.requiresClientIdentification = requiresClientIdentification;
    }
    
    public abstract void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame);
    
    private StompServer server;
    private Command command;
    private ConnectionContext context;
    private boolean requiresClientIdentification = true;


}
