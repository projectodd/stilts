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

import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.StompMessage;

public interface StompClient {
    
    public static enum State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
    }
    
    boolean isConnected();
    
    void connect() throws InterruptedException, StompException;
    void disconnect() throws InterruptedException, StompException;
    
    SubscriptionBuilder subscribe(String destination);
    void send(StompMessage message) throws StompException;
    
    ClientTransaction begin() throws StompException;
    void commit(String transactionId) throws StompException;
    void abort(String transactionId) throws StompException;
    
}
