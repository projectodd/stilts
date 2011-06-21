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

import javax.jms.Message;

import org.projectodd.stilts.stomp.spi.Acknowledger;

public class JMSMessageAcknowledger implements Acknowledger {

    public JMSMessageAcknowledger(Message message) {
        this.message = message;
    }

    @Override
    public void ack() throws Exception {
        this.message.acknowledge();
    }

    @Override
    public void nack() throws Exception {
        // nothing
    }

    private Message message;

}
