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

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class SubscribeHandler extends AbstractControlFrameHandler {

    public SubscribeHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.SUBSCRIBE );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info( "Subscribing for frame: " + frame );
        String destination = frame.getHeader( Header.DESTINATION );
        String id = frame.getHeader( Header.ID );
        try {
            getStompConnection().subscribe( destination, id, frame.getHeaders() );
        } catch (StompException e) {
            log.error( "Error performing subscription to '" + destination + "' for id '" + id + "'", e );
            sendError( channelContext, e.getMessage(), frame );
        }
    }

}
