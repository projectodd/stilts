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

package org.projectodd.stilts.stomplet.simple;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomplet.AcknowledgeableStomplet;
import org.projectodd.stilts.stomplet.Subscriber;

public class SimpleQueueStomplet extends SimpleSubscribableStomplet implements AcknowledgeableStomplet  {

    @Override
    public void onMessage(StompMessage message) throws StompException {
        sendToOneSubscriber( message );
    }

    @Override
    public void ack(Subscriber subscriber, StompMessage message) {
        // yay
    }

    @Override
    public void nack(Subscriber subscriber, StompMessage message) {
        try {
            sendToOneSubscriber( message );
        } catch (StompException e) {
            e.printStackTrace();
        }
    }

}
