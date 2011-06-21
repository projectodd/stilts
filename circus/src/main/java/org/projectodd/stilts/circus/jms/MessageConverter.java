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
