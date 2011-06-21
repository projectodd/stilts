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

package org.projectodd.stilts;

import org.projectodd.stilts.stomp.protocol.DefaultStompMessage;
import org.projectodd.stilts.stomp.spi.Headers;

public class StompMessages {

    private StompMessages() {

    }

    public static StompMessage createStompMessage() {
        return new DefaultStompMessage();
    }
    
    public static StompMessage createStompMessage(String destination, String content) {
        DefaultStompMessage message = new DefaultStompMessage();
        message.setDestination( destination );
        message.setContentAsString( content );
        return message;
    }

    public static StompMessage createStompMessage(String destination, Headers headers, String content) {
        DefaultStompMessage message = new DefaultStompMessage( headers, content );
        message.setDestination( destination );
        return message;
    }

}
