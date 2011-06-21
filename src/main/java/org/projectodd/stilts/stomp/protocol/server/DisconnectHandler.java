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

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.NotConnectedException;
import org.projectodd.stilts.spi.StompProvider;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class DisconnectHandler extends AbstractControlFrameHandler {

    public DisconnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.DISCONNECT );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            getStompConnection().disconnect();
        } catch (NotConnectedException e) {
            // ignore, we're shutting down anyhow
        }
        getContext().setActive( false );
        String receiptId = frame.getHeader( Header.RECEIPT );
        if (receiptId != null) {
            ChannelFuture future = channelContext.getChannel().write( StompFrames.newReceiptFrame( receiptId ) );
            future.addListener( ChannelFutureListener.CLOSE );
        } else {
            channelContext.getChannel().close();
        }
    }

}
