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

package org.projectodd.stilts.stomp.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.stomp.protocol.DefaultStompMessage;
import org.projectodd.stilts.stomp.spi.Acknowledger;
import org.projectodd.stilts.stomp.spi.Headers;

public class ClientStompMessage extends DefaultStompMessage {

    public ClientStompMessage(Headers headers, ChannelBuffer content, boolean isError) {
        super( headers, content, isError);
    }
    
    void setAcknowledger(Acknowledger acknowledger) {
        this.acknowledger = acknowledger;
    }

    @Override
    public void ack() throws StompException {
        if ( this.acknowledger != null ) {
            try {
                this.acknowledger.ack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        } else {
            super.ack();
        }
    }
    
    @Override
    public void nack() throws StompException {
        if ( this.acknowledger != null ) {
            try {
                this.acknowledger.nack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        } else {
            super.ack();
        }
    }

    private Acknowledger acknowledger;

}
