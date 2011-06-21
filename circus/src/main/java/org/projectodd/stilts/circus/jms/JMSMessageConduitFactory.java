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

package org.projectodd.stilts.circus.jms;

import javax.jms.XAConnection;
import javax.jms.XASession;

import org.projectodd.stilts.circus.xa.AbstractXAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public class JMSMessageConduitFactory extends AbstractXAMessageConduitFactory {

    public JMSMessageConduitFactory(XAConnection connection, DestinationMapper destinationMapper) {
        this.connection = connection;
        this.destinationMapper = destinationMapper;
    }
    
    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        XASession session = connection.createXASession();
        return new JMSMessageConduit( session, messageSink, this.destinationMapper );
    }
    
    private XAConnection connection;
    private DestinationMapper destinationMapper;

}
