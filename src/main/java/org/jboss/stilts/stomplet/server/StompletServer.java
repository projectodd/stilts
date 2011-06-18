package org.jboss.stilts.stomplet.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.transaction.TransactionManager;

import org.jboss.stilts.server.AbstractServer;
import org.jboss.stilts.stomplet.StompletContainer;
import org.jboss.stilts.stomplet.StompletStompProvider;
import org.jboss.stilts.xa.WrappedXAStompProvider;

import com.arjuna.ats.jta.common.jtaPropertyManager;

public class StompletServer extends AbstractServer {

    public static final int DEFAULT_PORT = 8675;
    private StompletContainer stompletContainer;

    public StompletServer() {
        this( DEFAULT_PORT, null );
    }

    public StompletServer(int port, ClassLoader classLoader) {
        super( port );
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = getClass().getClassLoader();
            }
        }
        this.stompletContainer = new StompletContainer( classLoader );
    }

    @Override
    public void start() throws Exception {
        if (getTransactionManager() == null) {
            setTransactionManager( createTransactionManager() );
        }

        StompletStompProvider provider = new StompletStompProvider( getTransactionManager(), this.stompletContainer );
        WrappedXAStompProvider xaProvider = new WrappedXAStompProvider( provider );
        provider.setXAStompProvider( xaProvider );
        //setStompProvider( new StompletStompProvider( getTransactionManager(), this.stompletContainer ) );
        setStompProvider( xaProvider );

        this.stompletContainer.start();
        super.start();
    }

    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    public static TransactionManager createTransactionManager() {
        return jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }

    public static void main(String[] args) throws Exception {
        StompletServer server = new StompletServer();

        Executor executor = Executors.newFixedThreadPool( 4 );
        server.setExecutor( executor );
        server.setTransactionManager( createTransactionManager() );

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
