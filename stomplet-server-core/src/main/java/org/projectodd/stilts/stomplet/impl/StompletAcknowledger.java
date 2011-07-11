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

package org.projectodd.stilts.stomplet.impl;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class StompletAcknowledger implements Acknowledger {

    public StompletAcknowledger(AcknowledgeableStomplet stomplet, Subscriber subscriber, StompMessage message) {
        this.stomplet = stomplet;
        this.subscriber = subscriber;
        this.message = message;
    }
    
    @Override
    public void ack() throws Exception {
        this.stomplet.ack( this.subscriber, this.message );
    }

    @Override
    public void nack() throws Exception {
        this.stomplet.nack( this.subscriber, this.message );
    }
    
    private AcknowledgeableStomplet stomplet;
    private Subscriber subscriber;
    private StompMessage message;

}
