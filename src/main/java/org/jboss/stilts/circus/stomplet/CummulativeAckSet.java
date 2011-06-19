package org.jboss.stilts.circus.stomplet;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.jboss.stilts.spi.Acknowledger;

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
