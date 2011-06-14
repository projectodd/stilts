package org.jboss.stilts.stomplet.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.stilts.StompException;
import org.jboss.stilts.server.AbstractServer;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.stomplet.StompletContainer;
import org.jboss.stilts.stomplet.StompletStompProvider;

public class StompletServer extends AbstractServer {

    public static final int DEFAULT_PORT = 8675;
    private StompletStompProvider provider;
    private StompletContainer stompletContainer;

    public StompletServer() {
        this( DEFAULT_PORT, null );
    }

    public StompletServer(int port, ClassLoader classLoader) {
        super( port );
        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if ( classLoader == null ) {
                classLoader = getClass().getClassLoader();
            }
        }
        this.stompletContainer = new StompletContainer( classLoader );
        this.provider = new StompletStompProvider( this.stompletContainer);
    }
    
    @Override
    public void start() throws Exception {
        this.provider.start();
        super.start();
    }

    @Override
    public StompProvider getStompProvider() throws Exception {
        return this.provider;
    }
    
    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    public static void main(String[] args) throws Exception {
        StompletServer server = new StompletServer();

        Executor executor = Executors.newFixedThreadPool( 4 );
        server.setExecutor( executor );
        try {
            server.start();
            while (true) {
                Thread.sleep( 1000 );
            }
        } catch (Throwable t) {
            server.stop();
        }

    }

}
