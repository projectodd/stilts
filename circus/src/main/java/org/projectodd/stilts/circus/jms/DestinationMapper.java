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

import org.projectodd.stilts.stomp.spi.Headers;

public interface DestinationMapper {
    
    /** For subscriptions. */
    DestinationSpec map(Session session, String destinationName, Headers headers) throws JMSException;
    
    /** For sending. */
    Destination map(Session session, String destinationName) throws JMSException;

}
