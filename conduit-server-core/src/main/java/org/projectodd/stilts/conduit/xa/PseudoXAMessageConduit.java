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

package org.projectodd.stilts.conduit.xa;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.logging.Logger;
import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.Subscription;
import org.projectodd.stilts.stomp.spi.StompSession;

public class PseudoXAMessageConduit implements MessageConduit {

    private static Logger log = Logger.getLogger(PseudoXAMessageConduit.class);

    public PseudoXAMessageConduit(TransactionManager transactionManager, PseudoXAResourceManager resourceManager) {
        this.transactionManager = transactionManager;
        this.resourceManager = resourceManager;
    }

    public StompSession getSession() {
        return this.resourceManager.getMessageConduit().getSession();
    }

    @Override
    public void send(StompMessage stompMessage) throws Exception {
        Transaction jtaTransaction = this.transactionManager.getTransaction();

        if (jtaTransaction != null) {
            jtaTransaction.enlistResource( this.resourceManager );
            PseudoXATransaction tx = this.resourceManager.currentTransaction();
            tx.addSentMessage( stompMessage );
        } else {
            log.debugf( "NO TX, forward SEND" );
            this.resourceManager.getMessageConduit().send( stompMessage );
        }
    }

    @Override
    public Subscription subscribe(String subscriptionId, String destination, Headers headers) throws Exception {
        return this.resourceManager.getMessageConduit().subscribe( subscriptionId, destination, headers );
    }

    private TransactionManager transactionManager;
    private PseudoXAResourceManager resourceManager;

}
