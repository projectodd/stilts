package org.jboss.stilts.protocol.server;

import java.util.HashMap;
import java.util.Map;

import org.jboss.stilts.spi.Acknowledger;

class AckManager {

    AckManager() {

    }

    void registerAcknowledger(String transactionId, String subscriptionId, String messageId, Acknowledger acknowledger) {
        this.acknowledgers.put( new Key( transactionId, subscriptionId, messageId), acknowledger);
    }

    Acknowledger removeAcknowledger(String transactionId, String subscriptionId, String messageId) {
        return this.acknowledgers.remove( new Key( transactionId, subscriptionId, messageId )  );
    }
    
    private final Map<Key, Acknowledger> acknowledgers = new HashMap<AckManager.Key, Acknowledger>();

    static class Key {
        private String transactionId;
        private String subscriptionId;
        private String messageId;

        Key(String transactionId, String subscriptionId, String messageId) {
            this.transactionId = transactionId;
            this.subscriptionId = subscriptionId;
            this.messageId = messageId;
        }

        public boolean equals(Object thatObj) {
            if ( thatObj instanceof Key ) {
                Key that = (Key) thatObj;
                return this.transactionId.equals( that.transactionId ) 
                    && this.subscriptionId.equals( that.subscriptionId ) 
                    && this.messageId.equals(  that.messageId );
            }
            return false;
        }
        
        public int hashCode() {
            return this.transactionId.hashCode() / 3 
                 + this.subscriptionId.hashCode() / 3
                 + this.messageId.hashCode() / 3;
        }
    }

}
