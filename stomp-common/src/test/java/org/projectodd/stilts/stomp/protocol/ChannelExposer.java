package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;

public class ChannelExposer implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    private Channel channel;

    public Channel getChannel() {
        return this.channel;
    }
    
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        this.channel = ctx.getChannel();
        ctx.getPipeline().remove( this );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        this.channel = ctx.getChannel();
        ctx.getPipeline().remove( this );
        
    }
}
