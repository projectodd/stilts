/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.client;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.client.StompClient.State;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.protocol.client.ClientContext;

public class DefaultClientContext implements ClientContext {
    
    public DefaultClientContext(AbstractStompClient client) {
        this.client = client;
    }

    @Override
    public LoggerManager getLoggerManager() {
        return this.client.getLoggerManager();
    }

    @Override
    public State getConnectionState() {
        return this.client.getConnectionState();
    }

    @Override
    public void setConnectionState(State connectionState) {
        this.client.setConnectionState( connectionState );
    }

    private AbstractStompClient client;

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

}
