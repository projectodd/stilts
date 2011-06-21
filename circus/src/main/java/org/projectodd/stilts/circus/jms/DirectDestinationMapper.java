/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
