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

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.DefaultStompMessage;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.StompMessageFactory;

public class DefaultStompMessageFactory implements StompMessageFactory {
    
    public final static DefaultStompMessageFactory INSTANCE = new DefaultStompMessageFactory();

    @Override
    public StompMessage createMessage(Headers headers, ChannelBuffer content, boolean isError) {
        return new DefaultStompMessage( headers, content, isError); 
    }

}
