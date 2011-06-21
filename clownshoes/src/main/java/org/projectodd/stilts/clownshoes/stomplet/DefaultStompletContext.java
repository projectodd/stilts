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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.HashMap;
import java.util.Map;

import org.projectodd.stilts.stomplet.MessageRouter;
import org.projectodd.stilts.stomplet.StompletContext;

public class DefaultStompletContext implements StompletContext {

    public DefaultStompletContext(MessageRouter messageRouter) {
        this.messageRouter = messageRouter;
    }
    @Override
    public String[] getAttributeNames() {
        return this.attributes.keySet().toArray( new String[this.attributes.size()] );
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(  name );
    }

    @Override
    public MessageRouter getMessageRouter() {
        return this.messageRouter;
    }

    private MessageRouter messageRouter;
    private Map<String, Object> attributes = new HashMap<String, Object>();

}
