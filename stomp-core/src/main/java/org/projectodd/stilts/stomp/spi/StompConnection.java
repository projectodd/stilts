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

package org.projectodd.stilts.stomp.spi;

import org.projectodd.stilts.NotConnectedException;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;

public interface StompConnection {
    
    String getSessionId();

    void send(StompMessage message, String transactionId) throws StompException;
    
    Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException;
    void unsubscribe(String subscriptionId, Headers headers) throws StompException;
    
    void begin(String transactionId, Headers headers) throws StompException;
    void commit(String transactionId) throws StompException;
    void abort(String transactionId) throws StompException;
    
    void ack(Acknowledger acknowledger, String transactionId) throws StompException;
    void nack(Acknowledger acknowledger, String transactionId) throws StompException;
    
    void disconnect() throws NotConnectedException;

    
    
}
