package org.projectodd.stilts.protocol.server;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.spi.Acknowledger;

class AckManager {

    AckManager() {

    }

    void registerAcknowledger(String messageId, Acknowledger acknowledger) {
        this.acknowledgers.put( messageId, acknowledger );
    }

    Acknowledger removeAcknowledger(String messageId) {
        return this.acknowledgers.remove( messageId );
    }

    private final Map<String, Acknowledger> acknowledgers = new HashMap<String, Acknowledger>();
}
