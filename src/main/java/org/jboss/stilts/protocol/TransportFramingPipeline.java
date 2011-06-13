package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.stilts.protocol.transport.StompFrameDecoder;
import org.jboss.stilts.protocol.transport.StompFrameEncoder;
import org.jboss.stilts.spi.StompServer;

public class TransportFramingPipeline extends DefaultChannelPipeline implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public TransportFramingPipeline(StompServer server) {
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
