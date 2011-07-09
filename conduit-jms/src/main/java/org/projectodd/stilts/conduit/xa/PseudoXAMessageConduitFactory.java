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

import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.conduit.spi.MessageConduitFactory;
import org.projectodd.stilts.conduit.spi.XAMessageConduit;
import org.projectodd.stilts.conduit.spi.XAMessageConduitFactory;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.Headers;

public class PseudoXAMessageConduitFactory implements XAMessageConduitFactory {

    private MessageConduitFactory factory;

    public PseudoXAMessageConduitFactory(MessageConduitFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        return this.factory.createMessageConduit( messageSink, headers );
    }

    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        PseudoXAAcknowledgeableMessageSink xaMessageSink = new PseudoXAAcknowledgeableMessageSink( messageSink );
        MessageConduit conduit = createMessageConduit( xaMessageSink, headers );
        PseudoXAResourceManager resourceManager = new PseudoXAResourceManager( conduit );
        xaMessageSink.setResourceManager( resourceManager );
        return new PseudoXAMessageConduit( resourceManager );
    }
}
