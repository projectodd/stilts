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
