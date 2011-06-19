package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.spi.StompProvider;

public abstract class AbstractControlFrameHandler extends AbstractProviderHandler {

    public AbstractControlFrameHandler(StompProvider provider, ConnectionContext context, Command command) {
        super( provider, context );
        this.command = command;
    }

    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompFrame) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        }
        super.messageReceived( channelContext, e );
    }

    protected void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        if (!frame.getCommand().equals( this.command )) {
            return;
        }

        if (this.requiresClientIdentification && getContext().getStompConnection() == null) {
            log.info( "Client not CONNECTED, closing connection" );
            sendErrorAndClose( channelContext, "Must CONNECT first", frame );
            return;
        }

        log.info( "FRAME command: " + frame.getCommand() );
        log.info( "         this: " + this.command );
        handleControlFrame( channelContext, frame );
    }

    protected void setRequiresClientIdentification(boolean requiresClientIdentification) {
        this.requiresClientIdentification = requiresClientIdentification;
    }

    public abstract void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame);

    private Command command;
    private ConnectionContext context;
    private boolean requiresClientIdentification = true;

}
