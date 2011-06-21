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

package org.projectodd.stilts.circus.server;

import javax.transaction.TransactionManager;

import org.projectodd.stilts.circus.CircusStompProvider;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;
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
            xaFactory = new PsuedoXAMessageConduitFactory( factory );
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
