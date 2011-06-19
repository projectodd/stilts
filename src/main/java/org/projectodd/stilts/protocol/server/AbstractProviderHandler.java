package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompProvider;

public abstract class AbstractProviderHandler extends SimpleChannelUpstreamHandler {

    public AbstractProviderHandler(StompProvider provider, ConnectionContext context) {
        this.provider = provider;
        this.context = context;
        this.log = context.getLoggerManager().getLogger( "stomp.protocol." + getClass().getSimpleName() );
    }
    
    public StompProvider getStompProvider() {
        return this.provider;
    }
    
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public StompConnection getStompConnection() {
        return this.context.getStompConnection();
    }
    
    protected ChannelFuture sendFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        return channelContext.getChannel().write(  frame  );
    }
    
    protected ChannelFuture sendError(ChannelHandlerContext channelContext, String message, StompFrame inReplyTo) {
        return sendFrame( channelContext, StompFrames.newErrorFrame( message, inReplyTo ) );
    }
    
    protected void sendErrorAndClose(ChannelHandlerContext channelContext, String message, StompFrame inReplyTo) {
        getContext().setActive( false );
        ChannelFuture future = sendError( channelContext, message, inReplyTo );
        future.addListener( ChannelFutureListener.CLOSE );
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error( "An error occurred", e.getCause() );
    }



    protected Logger log;
    
    private StompProvider provider;
    private ConnectionContext context;

}
