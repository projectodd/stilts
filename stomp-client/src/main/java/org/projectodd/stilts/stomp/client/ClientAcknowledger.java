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

package org.projectodd.stilts.stomp.client;

import org.projectodd.stilts.stomp.Acknowledger;
import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.TransactionalAcknowledger;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.protocol.StompFrames;

class ClientAcknowledger implements Acknowledger, TransactionalAcknowledger {

    ClientAcknowledger(StompClient client, Headers headers, Version version) {
        this.client = client;
        this.headers = headers;
        this.version = version;
    }

    @Override
    public void ack() throws Exception {
        ack( null );
    }

    @Override
    public void ack(String transactionId) throws Exception {
        DefaultHeaders ackHeaders = new DefaultHeaders();
        ackHeaders.put( Header.MESSAGE_ID, this.headers.get( Header.MESSAGE_ID ) );
        ackHeaders.put( Header.SUBSCRIPTION, this.headers.get( Header.SUBSCRIPTION ) );
        if (transactionId == null) {
            transactionId = this.headers.get( Header.TRANSACTION );
        }
        if (transactionId != null) {
            ackHeaders.put( Header.TRANSACTION, transactionId );
        }
        client.sendFrame( StompFrames.newAckFrame( ackHeaders ) );
    }

    @Override
    public void nack() throws Exception {
        nack( null );
    }

    @Override
    public void nack(String transactionId) throws Exception {
        if (version.isBefore( Version.VERSION_1_1 )) {
            throw new StompException("Cannot nack prior to STOMP version 1.1.");
        }
        
        DefaultHeaders nackHeaders = new DefaultHeaders();
        nackHeaders.put( Header.MESSAGE_ID, this.headers.get( Header.MESSAGE_ID ) );
        nackHeaders.put( Header.SUBSCRIPTION, this.headers.get( Header.SUBSCRIPTION ) );
        if (transactionId == null) {
            transactionId = this.headers.get( Header.TRANSACTION );
        }
        if (transactionId != null) {
            nackHeaders.put( Header.TRANSACTION, transactionId );
        }
        client.sendFrame( StompFrames.newNackFrame( nackHeaders ) );
    }

    private StompClient client;
    private Headers headers;
    private Version version;

}
