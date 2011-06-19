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

package org.projectodd.stilts.circus.xa.psuedo;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Acknowledger;

public class PsuedoXAAcknowledgeableMessageSink implements AcknowledgeableMessageSink {

    public PsuedoXAAcknowledgeableMessageSink(AcknowledgeableMessageSink sink) {
        this.sink = sink;
    }
    
    void setResourceManager(PsuedoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.sink.send( message );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        Acknowledger xaAcknowledger = new PsuedoXAAcknowledger( this.resourceManager, acknowledger );
        this.sink.send( message, xaAcknowledger );
    }
    
    private PsuedoXAResourceManager resourceManager;
    private AcknowledgeableMessageSink sink;

}
