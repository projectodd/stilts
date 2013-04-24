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

package org.projectodd.stilts.stomplet.container.xa;

import org.jboss.logging.Logger;
import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public class PseudoXAStompletAcknowledgeableMessageSink implements AcknowledgeableMessageSink {
    
    private static Logger log = Logger.getLogger(PseudoXAStompletAcknowledgeableMessageSink.class);

    public PseudoXAStompletAcknowledgeableMessageSink(PseudoXAStompletResourceManager resourceManager, AcknowledgeableMessageSink sink) {
        log.error( "PXAMessageSink ctor: " + resourceManager);
        this.resourceManager = resourceManager;
        this.sink = sink;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.sink.send( message );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        Acknowledger xaAcknowledger = new PseudoXAStompletAcknowledger( this.resourceManager, acknowledger );
        this.sink.send( message, xaAcknowledger );
    }
    
    public void close() {
        
    }
    
    private PseudoXAStompletResourceManager resourceManager;
    private AcknowledgeableMessageSink sink;

}
