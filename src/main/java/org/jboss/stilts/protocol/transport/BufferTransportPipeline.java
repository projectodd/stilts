package org.jboss.stilts.protocol.transport;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.DefaultChannelPipeline;

public class BufferTransportPipeline extends DefaultChannelPipeline implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public BufferTransportPipeline() {
        addLast( "stomp-frame-encoder", new StompFrameEncoder() );
        addLast( "stomp-frame-decoder", new StompFrameDecoder() );
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
