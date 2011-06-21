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

import java.net.URL;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;
import org.projectodd.stilts.logging.SimpleLoggerManager;

public class StandaloneCircusServer<T extends CircusServer> {

    public StandaloneCircusServer(T server) {
        this.server = server;
    }

    public void start() throws Throwable {
        startEmbeddedJCA();
        configure();
        this.server.start();
    }

    public void configure() throws Throwable {
        this.server.setExecutor( Executors.newFixedThreadPool( 4 ) );
        this.server.setLoggerManager( SimpleLoggerManager.DEFAULT_INSTANCE );
        
        this.context = new InitialContext();
        TransactionManager transactionManager = (TransactionManager) context.lookup( "java:/TransactionManager" );
        this.server.setTransactionManager( transactionManager );
    }

    private void startEmbeddedJCA() throws Throwable {
        System.setProperty( "java.naming.factory.initial", "org.jnp.interfaces.LocalOnlyContextFactory" );
        System.setProperty( "java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" );
        this.embeddedJca = EmbeddedFactory.create( true );
        this.embeddedJca.startup();
    }
    
    protected void deployResourceAdapter(URL url) throws Throwable {
        this.embeddedJca.deploy( url );
    }

    public void stop() throws Throwable {
        this.server.stop();
        System.err.println( "TM: " + this.server.getTransactionManager() );
        this.server.setTransactionManager( null );
        stopEmbeddedJCA();
    }

    private void stopEmbeddedJCA() throws Throwable {
        context.close();
        this.embeddedJca.shutdown();
        this.embeddedJca = null;
    }

    public T getServer() {
        return this.server;
    }

    private T server;
    private Embedded embeddedJca;
    private InitialContext context;

}
