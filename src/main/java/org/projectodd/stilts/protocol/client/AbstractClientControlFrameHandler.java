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

package org.projectodd.stilts.protocol.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;

public abstract class AbstractClientControlFrameHandler extends AbstractClientHandler {

    public AbstractClientControlFrameHandler(ClientContext clientContext, Command command) {
        super( clientContext );
        this.command = command;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        log.trace(  "received: " + e.getMessage() );
        if ( e.getMessage() instanceof StompFrame ) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        } 
        super.messageReceived( channelContext, e );
    }

    protected void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        if ( frame.getCommand().equals( this.command ) ) {
            handleControlFrame( channelContext, frame );
        }
    }
    
    protected abstract void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame);
    
    private Command command;


}
