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

package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public class StompletMessageConduitFactory implements MessageConduitFactory {

    private StompletContainer container;

    public StompletMessageConduitFactory(StompletContainer container) {
        this.container = container;
    }
    
    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return new StompletMessageConduit( this.container, messageSink );
    }

}
