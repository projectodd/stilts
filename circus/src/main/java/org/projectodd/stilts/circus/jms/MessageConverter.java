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
