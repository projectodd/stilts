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

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.Subscription;

public class PsuedoXAMessageConduit implements XAMessageConduit {

    public PsuedoXAMessageConduit(PsuedoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void send(StompMessage stompMessage) throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if ( tx == null ) {
            this.resourceManager.getMessageConduit().send( stompMessage );
        } else {
            tx.addSentMessage( stompMessage );
        }
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        return this.resourceManager.getMessageConduit().subscribe( subscriptionId, destination, headers );
    }

    @Override
    public XAResource getXAResource() {
        return this.resourceManager;
    }


    private PsuedoXAResourceManager resourceManager;

}
