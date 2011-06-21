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
                message.ack();
            } catch (StompException e) {
                e.printStackTrace();
            }
        } else if ( shouldNack ) {
            try {
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
