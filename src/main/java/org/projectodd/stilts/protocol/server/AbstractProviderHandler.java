/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompProvider;

public abstract class AbstractProviderHandler extends SimpleChannelUpstreamHandler {

    public AbstractProviderHandler(StompProvider provider, ConnectionContext context) {
        this.provider = provider;
        this.context = context;
        this.log = context.getLoggerManager().getLogger( "stomp.protocol." + getClass().getSimpleName() );
    }
    
    public StompProvider getStompProvider() {
        return this.provider;
    }
    
    public ConnectionContext getContext() {
        return this.context;
    }
    
    public StompConnection getStompConnection() {
        return this.context.getStompConnection();
    }
    
    protected ChannelFuture sendFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        return channelContext.getChannel().write(  frame  );
    }
    
    protected ChannelFuture sendError(ChannelHandlerContext channelContext, String message, StompFrame inReplyTo) {
        return sendFrame( channelContext, StompFrames.newErrorFrame( message, inReplyTo ) );
    }
    
    protected void sendErrorAndClose(ChannelHandlerContext channelContext, String message, StompFrame inReplyTo) {
        getContext().setActive( false );
        ChannelFuture future = sendError( channelContext, message, inReplyTo );
        future.addListener( ChannelFutureListener.CLOSE );
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error( "An error occurred", e.getCause() );
    }



    protected Logger log;
    
    private StompProvider provider;
    private ConnectionContext context;

}
