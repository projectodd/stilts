package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.stilts.logging.Logger;

public class DebugHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    private Logger log;
    
    public DebugHandler(Logger log) {
        this.log = log;
    }
    
    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log.trace( ">>outbound>> " + e );
        ctx.sendDownstream( e );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log.trace( "<<inbound<< " + e );
        if ( e instanceof ExceptionEvent ) {
            log.error( "EXCEPTION", ((ExceptionEvent)e).getCause() );
        }
        ctx.sendUpstream( e );
    }

}
