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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompProvider;

public class ConnectHandler extends AbstractControlFrameHandler {

    public ConnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.CONNECT );
        setRequiresClientIdentification( false );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            StompConnection clientAgent = getStompProvider().createConnection( new ChannelMessageSink( channelContext.getChannel(), getContext().getAckManager() ), frame.getHeaders() );
            if (clientAgent != null) {
                getContext().setStompConnection( clientAgent );
                log.info( "Set client-agent: " + getStompConnection() );
                StompFrame connected = StompFrames.newConnectedFrame( clientAgent.getSessionId() );
                log.info( "Replying with CONNECTED" );
                sendFrame( channelContext, connected );
            } else {
                sendErrorAndClose( channelContext, "Unable to connect", frame );
            }
        } catch (StompException e) {
            log.error( "Error connecting", e );
            sendErrorAndClose( channelContext, e.getMessage(), frame );
        }
    }

}
