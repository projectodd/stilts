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

package org.projectodd.stilts.stomp.protocol.client;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.stomp.client.StompClient.State;

public interface ClientContext {
    
    LoggerManager getLoggerManager();
    State getConnectionState();
    void setConnectionState(State state);
    
    void receiptReceived(String receiptId);
    void messageReceived(StompMessage message);
    void errorReceived(StompMessage message);
}
