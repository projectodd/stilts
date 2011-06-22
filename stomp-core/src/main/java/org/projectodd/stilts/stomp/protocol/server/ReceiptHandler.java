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
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class ReceiptHandler extends AbstractProviderHandler {

    public ReceiptHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelContext, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompFrame) {
            handleStompFrame( channelContext, (StompFrame) e.getMessage() );
        }
        super.messageReceived( channelContext, e );
    }

    public void handleStompFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        log.info(  "Checking receipt for: " + frame  );
        if ( ! getContext().isActive() ) {
            log.info( "Connection not active, no ACK required" );
            return;
        }
        String receiptId = frame.getHeader( Header.RECEIPT );
        if ( receiptId != null ) {
            StompFrame receipt = StompFrames.newReceiptFrame(receiptId);
            sendFrame( channelContext, receipt );
        }
    }
    
}
