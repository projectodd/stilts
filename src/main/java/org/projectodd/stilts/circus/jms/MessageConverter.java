package org.projectodd.stilts.circus.jms;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.spi.Headers;

public class MessageConverter {

    public static StompMessage convert(Message jmsMessage) throws JMSException {
        StompMessage stompMessage = StompMessages.createStompMessage();
        Enumeration<?> propNames = jmsMessage.getPropertyNames();

        Headers headers = stompMessage.getHeaders();
        while (propNames.hasMoreElements()) {
            String name = (String) propNames.nextElement();
            String value = jmsMessage.getStringProperty( name );
            if ((!excluded( name )) && (value != null)) {
                headers.put( name, value );
            }
        }

        return stompMessage;
    }

    public static Message convert(Session session, StompMessage stompMessage) throws JMSException {
        TextMessage jmsMessage = session.createTextMessage( stompMessage.getContentAsString() );
        Headers headers = stompMessage.getHeaders();
        for (String name : headers.getHeaderNames()) {
            if (!excluded( name )) {
                jmsMessage.setStringProperty( name, headers.get( name ) );
            }
        }
        return jmsMessage;
    }

    private static boolean excluded(String name) {
        if (name.indexOf( '-' ) >= 0) {
            return true;
        }
        
        if ( name.startsWith( "JMS"  ) ){
            return true;
        }

        return false;
    }

}
