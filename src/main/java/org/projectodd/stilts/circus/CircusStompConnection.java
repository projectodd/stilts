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

package org.projectodd.stilts.circus;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.projectodd.stilts.InvalidSubscriptionException;
import org.projectodd.stilts.InvalidTransactionException;
import org.projectodd.stilts.NotConnectedException;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.spi.Acknowledger;
import org.projectodd.stilts.spi.Headers;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompTransaction;
import org.projectodd.stilts.spi.Subscription;

public class CircusStompConnection implements StompConnection {

    public CircusStompConnection(CircusStompProvider stompProvider, XAMessageConduit messageConduit, String sessionId)
            throws StompException {
        this.stompProvider = stompProvider;
        this.messageConduit = messageConduit;
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    public CircusStompProvider getStompProvider() {
        return this.stompProvider;
    }
    
    public XAMessageConduit getMessageConduit() {
        return this.messageConduit;
    }

    public void send(StompMessage message, String transactionId) throws StompException {
        if (transactionId != null) {
            getTransaction( transactionId ).send( message );
        } else {
            send( message );
        }
    }
    
    protected void send(StompMessage message) throws StompException {
        try {
            this.messageConduit.send( message );
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public void ack(Acknowledger acknowledger, String transactionId) throws StompException {
        if (transactionId != null) {
            System.err.println( "ClientAgent.ack to transaction: " + transactionId );
            getTransaction( transactionId ).ack( acknowledger );
        } else {
            System.err.println( "ClientAgent.ack to provider" );
            try {
                acknowledger.ack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
    }

    @Override
    public void nack(Acknowledger acknowledger, String transactionId) throws StompException {
        if (transactionId != null) {
            System.err.println( "ClientAgent.send to transaction: " + transactionId );
            getTransaction( transactionId ).nack( acknowledger );
        } else {
            System.err.println( "ClientAgent.send to provider" );
            try {
                acknowledger.nack();
            } catch (Exception e) {
                throw new StompException( e );
            }
        }
    }

    CircusTransaction getTransaction(String transactionId) throws InvalidTransactionException {

        CircusTransaction transaction = this.namedTransactions.get( transactionId );

        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }

        return transaction;
    }

    CircusTransaction removeTransaction(String transactionId) {
        return this.namedTransactions.remove( transactionId );
    }

    @Override
    public void begin(String transactionId, Headers headers) throws StompException {

        Transaction jtaTransaction = null;
        TransactionManager tm = getStompProvider().getTransactionManager();
        try {
            tm.begin();
            jtaTransaction = tm.getTransaction();
            tm.suspend();
        } catch (NotSupportedException e) {
            throw new StompException( e );
        } catch (SystemException e) {
            throw new StompException( e );
        }

        try {
            CircusTransaction transaction = createTransaction( jtaTransaction, transactionId );
            System.err.println( "NAMED TX: " + transaction );
            this.namedTransactions.put( transactionId, transaction );
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    @Override
    public void commit(String transactionId) throws StompException {
        StompTransaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        transaction.commit();
    }

    @Override
    public void abort(String transactionId) throws StompException {
        StompTransaction transaction = removeTransaction( transactionId );
        if (transaction == null) {
            throw new InvalidTransactionException( transactionId );
        }
        transaction.abort();
    }

    @Override
    public synchronized Subscription subscribe(String destination, String subscriptionId, Headers headers) throws StompException {
        try {
            Subscription subscription = createSubscription( destination, subscriptionId, headers );
            if (subscription == null) {
                return null;
            }
            this.subscriptions.put( subscription.getId(), subscription );
            return subscription;
        } catch (Exception e) {
            throw new StompException( e );
        }
    }

    public Subscription createSubscription(String destination, String subscriptionId, Headers headers) throws Exception {
        return this.messageConduit.subscribe( subscriptionId, destination, headers );
    }

    @Override
    public synchronized void unsubscribe(String id, Headers headers) throws StompException {
        Subscription subscription = this.subscriptions.remove( id );
        if (subscription == null) {
            throw new InvalidSubscriptionException( id );
        }
    }

    @Override
    public synchronized void disconnect() throws NotConnectedException {
        for (StompTransaction each : this.namedTransactions.values()) {
            try {
                each.abort();
            } catch (StompException e) {
                e.printStackTrace();
            }
        }
    }

    protected CircusTransaction createTransaction(Transaction jtaTransaction, String transactionId) throws Exception {
        return new CircusTransaction( this, jtaTransaction, transactionId );
    }

    private Map<String, Subscription> subscriptions = new HashMap<String, Subscription>();

    private Map<String, CircusTransaction> namedTransactions = new HashMap<String, CircusTransaction>();

    private XAMessageConduit messageConduit;
    private CircusStompProvider stompProvider;
    private String sessionId;
}
