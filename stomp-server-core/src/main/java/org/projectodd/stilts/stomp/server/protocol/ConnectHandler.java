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
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.spi.StompConnection;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class ConnectHandler extends AbstractControlFrameHandler {
	
	private static Logger log = Logger.getLogger(ConnectHandler.class);

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
                StompFrame connected = StompFrames.newConnectedFrame( clientAgent.getSessionId() );
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
