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
