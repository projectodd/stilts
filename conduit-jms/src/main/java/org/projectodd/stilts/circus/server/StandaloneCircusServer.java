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

import java.net.URL;
import java.util.concurrent.Executors;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;

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
        this.server.setChannelExecutor( Executors.newFixedThreadPool( 4 ) );
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
