package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.StompServer;

public abstract class AbstractServerHandler extends SimpleChannelUpstreamHandler {

    public AbstractServerHandler(StompServer server, ConnectionContext context) {
        this.server = server;
        this.context = context;
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
    
    private StompServer server;
    private ConnectionContext context;

}
