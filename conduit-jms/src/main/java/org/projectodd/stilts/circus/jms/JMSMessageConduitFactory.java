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

package org.projectodd.stilts.circus.jms;

import javax.jms.XAConnection;
import javax.jms.XASession;

import org.projectodd.stilts.circus.xa.AbstractXAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomp.spi.Headers;

public class JMSMessageConduitFactory extends AbstractXAMessageConduitFactory {

    public JMSMessageConduitFactory(XAConnection connection, DestinationMapper destinationMapper) {
        this.connection = connection;
        this.destinationMapper = destinationMapper;
    }
    
    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        XASession session = connection.createXASession();
        return new JMSMessageConduit( session, messageSink, this.destinationMapper );
    }
    
    private XAConnection connection;
    private DestinationMapper destinationMapper;

}
