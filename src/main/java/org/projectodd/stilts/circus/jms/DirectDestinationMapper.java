package org.projectodd.stilts.circus.jms;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.projectodd.stilts.protocol.StompFrame.Header;
import org.projectodd.stilts.spi.Headers;

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
