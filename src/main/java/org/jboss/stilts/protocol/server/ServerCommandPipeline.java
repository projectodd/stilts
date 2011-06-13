package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.stilts.protocol.StompServerChannelHandler;
import org.jboss.stilts.spi.StompServer;

public class ServerCommandPipeline extends DefaultChannelPipeline implements StompServerChannelHandler, ChannelUpstreamHandler, ChannelDownstreamHandler {

    private StompServer server;

    public ServerCommandPipeline() {
    }
    
    public void setServer(StompServer server) {
        this.server = server;
    }
    
    public void initialize() {
        ConnectionContext context = new ConnectionContext();
        addLast( "stomp-server-connect", new ConnectHandler( server, context ) );
        addLast( "stomp-server-disconnect", new DisconnectHandler( server, context ) );

        addLast( "stomp-server-subscribe", new SubscribeHandler( server, context ) );
        addLast( "stomp-server-unsubscribe", new UnsubscribeHandler( server, context ) );

        addLast( "stomp-server-begin", new BeginHandler( server, context ) );
        addLast( "stomp-server-commit", new CommitHandler( server, context ) );
        addLast( "stomp-server-abort", new AbortHandler( server, context ) );
        
        addLast( "stomp-server-send", new SendHandler( server, context ) );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        sendDownstream( e );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        sendUpstream( e );
    }

}
