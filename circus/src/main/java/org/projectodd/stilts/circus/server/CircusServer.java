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

import org.projectodd.stilts.circus.CircusStompProvider;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.pseudo.PseudoXAMessageConduitFactory;
import org.projectodd.stilts.server.SimpleStompServer;

public class CircusServer extends SimpleStompServer<CircusStompProvider> {

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
    
    public void setMessageConduitFactory(XAMessageConduitFactory messageConduitFactory) {
        this.messageConduitFactory = messageConduitFactory;
    }
    
    public XAMessageConduitFactory getMessageConduitFactory() {
        return this.messageConduitFactory;
    }
    
    @Override
    public void start() throws Throwable {
        MessageConduitFactory factory = this.messageConduitFactory;
        XAMessageConduitFactory xaFactory = null;
        
        if ( factory instanceof XAMessageConduitFactory ) {
            xaFactory = (XAMessageConduitFactory) factory;
        } else {
            xaFactory = new PseudoXAMessageConduitFactory( factory );
        }
        
        CircusStompProvider provider = new CircusStompProvider( this.transactionManager, xaFactory );
        setStompProvider( provider );
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
        getStompProvider().stop();
    }

    private TransactionManager transactionManager;
    private XAMessageConduitFactory messageConduitFactory;

}
