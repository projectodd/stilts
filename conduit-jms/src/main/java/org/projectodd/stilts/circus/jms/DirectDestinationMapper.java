/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
