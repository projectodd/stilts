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

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class DefaultClientTransaction implements ClientTransaction {

    public DefaultClientTransaction(SimpleStompClient client, String id) {
        this( client, id, false );
    }

    public DefaultClientTransaction(SimpleStompClient client, String id, boolean isGlobal) {
        this.client = client;
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void send(StompMessage message) {
        message.getHeaders().put( Header.TRANSACTION, this.id );
        this.client.send( message );
    }

    @Override
    public void commit() throws StompException {
        this.client.commit( this.id );
    }

    @Override
    public void abort() throws StompException {
        this.client.abort( this.id );
    }

    private SimpleStompClient client;
    private String id;
}
