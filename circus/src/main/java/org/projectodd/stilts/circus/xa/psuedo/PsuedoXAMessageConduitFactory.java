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

package org.projectodd.stilts.circus.xa.psuedo;

import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public class PsuedoXAMessageConduitFactory implements XAMessageConduitFactory {

    private MessageConduitFactory factory;

    public PsuedoXAMessageConduitFactory(MessageConduitFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return this.factory.createMessageConduit( messageSink );
    }

    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        PsuedoXAAcknowledgeableMessageSink xaMessageSink = new PsuedoXAAcknowledgeableMessageSink( messageSink );
        MessageConduit conduit = createMessageConduit( xaMessageSink );
        PsuedoXAResourceManager resourceManager = new PsuedoXAResourceManager( conduit );
        xaMessageSink.setResourceManager( resourceManager );
        return new PsuedoXAMessageConduit( resourceManager );
    }
}
