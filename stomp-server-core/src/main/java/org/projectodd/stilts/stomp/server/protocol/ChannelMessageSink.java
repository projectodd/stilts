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

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

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
