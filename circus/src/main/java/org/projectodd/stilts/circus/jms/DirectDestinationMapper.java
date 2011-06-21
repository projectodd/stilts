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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.Headers;

public class DirectDestinationMapper implements DestinationMapper {
    
    public static final DirectDestinationMapper INSTANCE = new DirectDestinationMapper();
    
    @Override
    public DestinationSpec map(Session session, String destinationName, Headers headers) throws JMSException {
        Destination destination = map( session, destinationName );
        String selector = headers.get( Header.SELECTOR );
        
        return new DestinationSpec( destination, selector );
    }

    @Override
    public Destination map(Session session, String destinationName) throws JMSException {
        Destination destination = null;

        if (destinationName.contains( "queue" )) {
            destination = session.createQueue( destinationName );
        } else {
            destination = session.createTopic( destinationName );
        }
        
        return destination;
    }

}
