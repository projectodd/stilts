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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.projectodd.stilts.spi.Acknowledger;

public class CummulativeAckSet implements AckSet {

    public CummulativeAckSet() {

    }

    @Override
    public synchronized void ack(String messageId) throws Exception {
        if ( hasMessageId( messageId) ) {
            ListIterator<Entry> iter = this.acknowledgers.listIterator();
            while ( iter.hasNext() ) {
                Entry entry = iter.next();
                iter.remove();
                entry.acknowledger.ack();
            }
        }
    }
    
    @Override
    public synchronized void nak(String messageId) throws Exception {
        if ( hasMessageId( messageId) ) {
            ListIterator<Entry> iter = this.acknowledgers.listIterator();
            while ( iter.hasNext() ) {
                Entry entry = iter.next();
                iter.remove();
                entry.acknowledger.ack();
            }
        }
    }
    
    protected boolean hasMessageId(String messageId) {
        for ( Entry each : this.acknowledgers ) {
            if ( each.messageId.equals( messageId ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void addAcknowledger(String messageId, Acknowledger acknowledger) {
        acknowledgers.add( new Entry(messageId, acknowledger) );
    }

    class Entry {
        public Entry(String messageId, Acknowledger acknowledger) {
            this.messageId = messageId;
            this.acknowledger = acknowledger;
        }

        public String messageId;
        public Acknowledger acknowledger;
    }

    private List<Entry> acknowledgers = new LinkedList<CummulativeAckSet.Entry>();
}
