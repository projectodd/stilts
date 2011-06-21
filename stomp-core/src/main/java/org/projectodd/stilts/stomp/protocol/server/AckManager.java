/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.protocol.server;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.stomp.spi.Acknowledger;

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
