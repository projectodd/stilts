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

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class ChannelMessageSink implements AcknowledgeableMessageSink {

    public ChannelMessageSink(Channel channel, AckManager ackManager) {
        this.channel = channel;
        this.ackManager = ackManager;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        send( message, null );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        if ( message.getId() == null ) {
            message.getHeaders().put( Header.MESSAGE_ID, getNextMessageId() );
        }
        if (acknowledger != null) {
            this.ackManager.registerAcknowledger( message.getId(), acknowledger );
        }
        this.channel.write( message );
    }
    
    private static String getNextMessageId() {
        return "message-" + MESSAGE_COUNTER.getAndIncrement();
    }

    private Channel channel;
    private AckManager ackManager;
    
    private static final AtomicLong MESSAGE_COUNTER = new AtomicLong();

}
