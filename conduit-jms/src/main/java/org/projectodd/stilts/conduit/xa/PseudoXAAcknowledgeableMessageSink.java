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

package org.projectodd.stilts.conduit.xa;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.Acknowledger;

public class PseudoXAAcknowledgeableMessageSink implements AcknowledgeableMessageSink {

    public PseudoXAAcknowledgeableMessageSink(AcknowledgeableMessageSink sink) {
        this.sink = sink;
    }
    
    void setResourceManager(PseudoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.sink.send( message );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        Acknowledger xaAcknowledger = new PseudoXAAcknowledger( this.resourceManager, acknowledger );
        this.sink.send( message, xaAcknowledger );
    }
    
    private PseudoXAResourceManager resourceManager;
    private AcknowledgeableMessageSink sink;

}
