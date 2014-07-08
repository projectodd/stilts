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

import java.net.InetSocketAddress;
import java.net.URI;

import javax.net.ssl.SSLContext;

import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.client.protocol.ClientContext;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;

class ClientContextImpl implements ClientContext {

    ClientContextImpl(StompClient client) {
        this.client = client;
    }

    @Override
    public InetSocketAddress getServerAddress() {
        return this.client.getServerAddress();
    }

    @Override
    public URI getWebSocketAddress() {
        return client.getWebSocketAddress();
    }

    @Override
    public State getConnectionState() {
        return this.client.getConnectionState();
    }

    @Override
    public Version getVersion() {
        return this.client.getVersion();
    }

    @Override
    public boolean isSecure() {
    	return this.client.isSecure();
    }

    @Override
    public void setConnectionState(State connectionState) {
        this.client.setConnectionState( connectionState );
    }

    @Override
    public void setVersion(Version version) {
        this.client.setVersion( version );
    }

    @Override
    public void messageReceived(StompMessage message) {
        this.client.messageReceived( message );
    }

    @Override
    public void errorReceived(StompMessage message) {
        this.client.errorReceived( message );
    }

    @Override
    public void receiptReceived(String receiptId) {
        this.client.receiptReceived( receiptId );
    }

    @Override
    public SSLContext getSSLContext() {
    	return this.client.getSSLContext();
    }

    private StompClient client;

}
