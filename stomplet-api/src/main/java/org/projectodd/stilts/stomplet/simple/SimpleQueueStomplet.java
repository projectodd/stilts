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

package org.projectodd.stilts.stomplet.simple;

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.spi.StompSession;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class SimpleQueueStomplet extends SimpleSubscribableStomplet implements AcknowledgeableStomplet  {

    @Override
    public void onMessage(StompMessage message, StompSession session) throws StompException {
        sendToOneSubscriber( message );
    }

    @Override
    public void ack(Subscriber subscriber, StompMessage message) {
        // yay
    }

    @Override
    public void nack(Subscriber subscriber, StompMessage message) {
        try {
            sendToOneSubscriber( message );
        } catch (StompException e) {
            e.printStackTrace();
        }
    }

}
