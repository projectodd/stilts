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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.StompProvider;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class SendHandler extends AbstractProviderHandler {

    public SendHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        log.info( "SEND: " + e.getMessage() );
        if (e.getMessage() instanceof StompMessage) {
            log.info( "SEND: " + e.getMessage() + " via " + getContext()  );
            log.info( "SEND: " + e.getMessage() + " via " + getContext().getStompConnection()  );
            StompMessage message = (StompMessage) e.getMessage();
            String transactionId = message.getHeaders().get( Header.TRANSACTION );
            getContext().getStompConnection().send( (StompMessage) e.getMessage(), transactionId );
        }
        super.messageReceived( ctx, e );
    }

}
