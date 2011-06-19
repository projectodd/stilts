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

package org.projectodd.stilts.circus.stomplet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.projectodd.stilts.spi.Acknowledger;

public class IndividualAckSet implements AckSet {

    public IndividualAckSet() {
        
    }

    @Override
    public void ack(String messageId) throws Exception {
        Acknowledger ack = this.acknowledgers.remove( messageId );
        if ( ack != null ) {
            ack.ack();
        }
    }

    @Override
    public void nak(String messageId) throws Exception {
        Acknowledger ack = this.acknowledgers.remove( messageId );
        if ( ack != null ) {
            ack.nack();
        }
    }

    @Override
    public void addAcknowledger(String messageId, Acknowledger acknowledger) {
        this.acknowledgers.put( messageId, acknowledger );
    }
    
    private Map<String, Acknowledger> acknowledgers = new ConcurrentHashMap<String, Acknowledger>();
}
