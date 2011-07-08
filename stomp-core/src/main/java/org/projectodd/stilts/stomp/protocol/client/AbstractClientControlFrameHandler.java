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

package org.projectodd.stilts.stomp.protocol.client;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;

public abstract class AbstractClientControlFrameHandler extends AbstractClientHandler {

	private static Logger log = Logger.getLogger(AbstractClientControlFrameHandler.class);
	
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
