package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.stilts.spi.StompServer;

public class MessageFramingPipeline extends DefaultChannelPipeline implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public MessageFramingPipeline(StompServer server) {
        addLast( "stomp-message-encoder", new StompServerMessageEncoder() );
        addLast( "stomp-message-decoder", new StompMessageDecoder() );
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
