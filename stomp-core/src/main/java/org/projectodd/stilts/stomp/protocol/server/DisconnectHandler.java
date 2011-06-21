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

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.NotConnectedException;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

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
