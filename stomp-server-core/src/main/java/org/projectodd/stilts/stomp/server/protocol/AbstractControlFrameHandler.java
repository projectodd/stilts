/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.Heartbeat;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.spi.StompConnection;
import org.projectodd.stilts.stomp.spi.StompProvider;

public abstract class AbstractControlFrameHandler extends AbstractProviderHandler {

    private static final Logger log = Logger.getLogger( AbstractControlFrameHandler.class );

    public AbstractControlFrameHandler(StompProvider provider, ConnectionContext context, Command command) {
        super( provider, context );
        this.command = command;
    }

    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompFrame) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        }
        StompConnection connection = getStompConnection();
        if (connection != null) {
            Heartbeat hb = connection.getHeartbeat();
            if (hb != null) {
                hb.touch();
            }
        }
        super.messageReceived( channelContext, e );
    }

    protected void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        if (!frame.getCommand().equals( this.command )) {
            return;
        }

        if (this.requiresClientIdentification && getContext().getStompConnection() == null) {
            log.warn( "Client not CONNECTED, closing connection" );
            sendErrorAndClose( channelContext, "Must CONNECT first", frame );
            return;
        }

        handleControlFrame( channelContext, frame );
    }

    protected void setRequiresClientIdentification(boolean requiresClientIdentification) {
        this.requiresClientIdentification = requiresClientIdentification;
    }

    public abstract void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame);

    private Command command;
    private boolean requiresClientIdentification = true;

}
