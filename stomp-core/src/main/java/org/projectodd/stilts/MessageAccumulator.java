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

package org.projectodd.stilts;

import java.util.ArrayList;
import java.util.List;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.client.MessageHandler;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class MessageAccumulator implements MessageHandler {
    private ArrayList<StompMessage> messages;
    private boolean shouldAck;
    private boolean shouldNack;

    public MessageAccumulator() {
        this(false, false);
    }
    
    public MessageAccumulator(boolean shouldAck, boolean shouldNack) {
        this.shouldAck = shouldAck;
        this.shouldNack = shouldNack;
        this.messages = new ArrayList<StompMessage>();
    }

    public List<String> messageIds() {
        List<String> messageIds = new ArrayList<String>();
        for (StompMessage each : this.messages) {
            messageIds.add( each.getHeaders().get( Header.MESSAGE_ID ) );
        }
        return messageIds;
    }

    public void handle(StompMessage message) {
        this.messages.add( message );
        if ( shouldAck ) {
            try {
                System.err.println( "Send ACK" );
                message.ack();
            } catch (StompException e) {
                e.printStackTrace();
            }
        } else if ( shouldNack ) {
            try {
                System.err.println( "Send NACK" );
                message.nack();
            } catch (StompException e) {
                e.printStackTrace();
            }
        }
    }

    public List<StompMessage> getMessage() {
        return this.messages;
    }

    public int size() {
        return this.messages.size();
    }

    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    public void clear() {
        this.messages.clear();
    }

    public String toString() {
        return this.messages.toString();
    }

}
