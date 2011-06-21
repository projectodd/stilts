/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.projectodd.stilts.logging.Logger;

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
