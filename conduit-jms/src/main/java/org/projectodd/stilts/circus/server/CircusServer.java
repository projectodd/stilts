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

package org.projectodd.stilts.circus.server;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.conduit.MessageConduitFactory;
import org.projectodd.stilts.conduit.stomp.ConduitStompProvider;
import org.projectodd.stilts.conduit.xa.PseudoXAMessageConduitFactory;
import org.projectodd.stilts.server.SimpleStompServer;

/** Adapts basic STOMP server to simpler <code>MessageConduit</code> interface.
 * 
 * @author Bob McWhirter
 */
public class CircusServer<T extends MessageConduitFactory> extends SimpleStompServer<ConduitStompProvider> {

    public CircusServer() {
        super();
    }
    
    /**
     * Construct with a port.
     * 
     * @param port The listen port to bind to.
     */
    public CircusServer(int port) {
        super( port );
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }
    
    public void setMessageConduitFactory(T messageConduitFactory) {
        this.messageConduitFactory = messageConduitFactory;
        if ( messageConduitFactory instanceof XAMessageConduitFactory ) {
            this.xaMessageConduitFactory = (XAMessageConduitFactory) messageConduitFactory;
        } else {
            this.xaMessageConduitFactory = new PseudoXAMessageConduitFactory( messageConduitFactory );
        }
    }
    
    public T getMessageConduitFactory() {
        return this.messageConduitFactory;
    }
    
    public XAMessageConduitFactory getXAMessageConduitFactory() {
        return this.xaMessageConduitFactory;
    }
    
    @Override
    public void start() throws Throwable {
        ConduitStompProvider provider = new ConduitStompProvider( this.transactionManager, getXAMessageConduitFactory() );
        setStompProvider( provider );
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
        getStompProvider().stop();
    }

    private TransactionManager transactionManager;
    private T messageConduitFactory;
    private XAMessageConduitFactory xaMessageConduitFactory;

}
