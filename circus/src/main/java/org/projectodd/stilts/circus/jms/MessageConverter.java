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

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.StompMessages;
import org.projectodd.stilts.stomp.spi.Headers;

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
