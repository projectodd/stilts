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
