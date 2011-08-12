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

package org.projectodd.stilts.conduit;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.conduit.spi.MessageConduitFactory;
import org.projectodd.stilts.conduit.spi.TransactionalMessageConduitFactory;
import org.projectodd.stilts.conduit.stomp.ConduitStompProvider;
import org.projectodd.stilts.conduit.xa.PseudoXAMessageConduitFactory;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.server.StompServer;

/** Adapts basic STOMP server to simpler <code>MessageConduit</code> interface.
 * 
 * <p>A ConduitServer applies JTA semantics and requires a <code>TransactionManager</code>.</p>
 * 
 * @author Bob McWhirter
 */
public class ConduitServer<T extends MessageConduitFactory> {

    
	public ConduitServer() {
		this( Constants.DEFAULT_PORT );
    }
    
    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public ConduitServer(int port) {
    	this.server = new StompServer<ConduitStompProvider>( port );
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setMessageConduitFactory(T messageConduitFactory) {
        this.messageConduitFactory = messageConduitFactory;
        if ( messageConduitFactory instanceof TransactionalMessageConduitFactory ) {
            this.transactionalMessageConduitFactory = (TransactionalMessageConduitFactory) messageConduitFactory;
        } else {
            System.err.println( "WRAPPING WITH PSEUDO XA CONDUIT" );
            this.transactionalMessageConduitFactory = new PseudoXAMessageConduitFactory( messageConduitFactory );
        }
    }
    
    public T getMessageConduitFactory() {
        return this.messageConduitFactory;
    }
    
    public TransactionalMessageConduitFactory getTransactionalMessageConduitFactory() {
        return this.transactionalMessageConduitFactory;
    }
    
    public void start() throws Exception {
        System.err.println( "CONDUIT_SERVER.start " + this.transactionManager );
        this.transactionalMessageConduitFactory.setTransactionManager( this.transactionManager );
        ConduitStompProvider provider = new ConduitStompProvider( this.transactionManager, getTransactionalMessageConduitFactory() );
        this.server.setStompProvider( provider );
        this.server.start();
    }
    
    public void stop() throws Exception {
    	this.server.stop();
    }

    private StompServer<ConduitStompProvider> server;
    private TransactionManager transactionManager;
    private T messageConduitFactory;
    private TransactionalMessageConduitFactory transactionalMessageConduitFactory;

}
