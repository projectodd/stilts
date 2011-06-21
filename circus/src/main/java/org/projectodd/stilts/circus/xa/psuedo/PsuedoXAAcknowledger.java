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

import org.projectodd.stilts.stomp.spi.Acknowledger;

public class PsuedoXAAcknowledger implements Acknowledger {

    public PsuedoXAAcknowledger(PsuedoXAResourceManager resourceManager, Acknowledger acknowledger) {
        this.resourceManager = resourceManager;
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addAck( this.acknowledger );
        } else {
            this.acknowledger.ack();
        }
    }

    @Override
    public void nack() throws Exception {
        PsuedoXATransaction tx = this.resourceManager.currentTransaction();
        if (tx != null) {
            tx.addNack(  this.acknowledger );
        } else {
            this.acknowledger.nack();
        }
    }
    
    private PsuedoXAResourceManager resourceManager;
    private Acknowledger acknowledger;


}
