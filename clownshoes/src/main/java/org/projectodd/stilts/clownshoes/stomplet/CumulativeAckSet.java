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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.projectodd.stilts.stomp.spi.Acknowledger;

public class CumulativeAckSet implements AckSet {

    public CumulativeAckSet() {

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

    private List<Entry> acknowledgers = new LinkedList<CumulativeAckSet.Entry>();
}
