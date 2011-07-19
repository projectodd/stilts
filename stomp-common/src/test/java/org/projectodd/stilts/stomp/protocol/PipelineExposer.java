package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;

public class PipelineExposer extends SimpleChannelHandler {

    private ChannelPipeline pipeline;

    public ChannelPipeline getPipeline() {
        return this.pipeline;
    }
    
    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        this.pipeline = ctx.getPipeline();
        this.pipeline.remove( this );
        super.handleUpstream( ctx, e );
    }
}
