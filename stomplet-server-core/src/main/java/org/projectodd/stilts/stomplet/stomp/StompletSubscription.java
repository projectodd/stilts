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

package org.projectodd.stilts.stomplet.stomp;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomplet.Stomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class StompletSubscription implements Subscription, AcknowledgeableMessageSink {

    public StompletSubscription(Stomplet stomplet, Subscriber subscriber) {
        this.stomplet = stomplet;
        this.subscriber = subscriber;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void cancel() throws StompException {
        stomplet.onUnsubscribe( this.subscriber );
    }

    @Override
    public void send(StompMessage message) throws StompException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        // TODO Auto-generated method stub
        
    }
    
    private Stomplet stomplet;
    private Subscriber subscriber;

}
