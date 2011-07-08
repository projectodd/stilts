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

package org.projectodd.stilts.circus.xa.pseudo;

import javax.transaction.xa.XAResource;

import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.stomp.spi.Headers;
import org.projectodd.stilts.stomp.spi.Subscription;

public class PseudoXAMessageConduit implements XAMessageConduit {

    public PseudoXAMessageConduit(PseudoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void send(StompMessage stompMessage) throws Exception {
        PseudoXATransaction tx = this.resourceManager.currentTransaction();
        if ( tx == null ) {
            this.resourceManager.getMessageConduit().send( stompMessage );
        } else {
            tx.addSentMessage( stompMessage );
        }
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        return this.resourceManager.getMessageConduit().subscribe( subscriptionId, destination, headers );
    }

    @Override
    public XAResource getXAResource() {
        return this.resourceManager;
    }


    private PseudoXAResourceManager resourceManager;

}
