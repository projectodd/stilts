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

package org.projectodd.stilts.stomp.protocol.client;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public abstract class AbstractClientHandler extends SimpleChannelUpstreamHandler {

    public AbstractClientHandler(ClientContext clientContext) {
        this.clientContext = clientContext;
        this.log = clientContext.getLoggerManager().getLogger( "stomp.protocol." + getClass().getSimpleName() );
    }
    
    public ClientContext getClientContext() {
        return this.clientContext;
    }
    
    protected ChannelFuture sendFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        return channelContext.getChannel().write(  frame  );
    }
    
    protected ChannelFuture sendError(ChannelHandlerContext channelContext, String message) {
        return sendFrame( channelContext, StompFrames.newErrorFrame( message, null ) );
    }
    
    protected void sendErrorAndClose(ChannelHandlerContext channelContext, String message) {
        ChannelFuture future = sendError( channelContext, message );
        future.addListener( ChannelFutureListener.CLOSE );
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error( "An error occurred", e.getCause() );
    }

    protected Logger log;
    private ClientContext clientContext;

}
