package org.jboss.stilts.circus;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.TransactionManager;

import org.jboss.stilts.MessageAccumulator;
import org.jboss.stilts.circus.server.AbstractCircusServer;
import org.jboss.stilts.client.AbstractStompClient;
import org.jboss.stilts.logging.SimpleLoggerManager;
import org.jboss.stilts.logging.SimpleLoggerManager.Level;
import org.jboss.stilts.server.BasicStompServer;
import org.junit.After;
import org.junit.Before;

import com.arjuna.ats.jta.common.jtaPropertyManager;

public abstract class AbstractCircusClientServerTest<T extends AbstractCircusServer> {

    public static Level SERVER_ROOT_LEVEL = Level.INFO;
    public static Level CLIENT_ROOT_LEVEL = Level.NONE;

    private TransactionManager transactionManager;
    private T server;
    protected SimpleLoggerManager serverLoggerManager;
    protected SimpleLoggerManager clientLoggerManager;
    protected AbstractStompClient client;

    private final Map<String, MessageAccumulator> accumulators = new HashMap<String, MessageAccumulator>();

    @Before
    public void resetAccumulators() {
        this.accumulators.clear();
    }

    @Before
    public void startServer() throws Exception {
        setUpServerLoggerManager();
        setUpTransactionManager();
        this.server = createServer();
        this.server.setTransactionManager( this.transactionManager );
        this.server.start();
        prepareServer();
    }
    
    protected void setUpTransactionManager() {
        this.transactionManager = jtaPropertyManager.getJTAEnvironmentBean().getTransactionManager();
    }
    
    public void setUpServerLoggerManager() {
        this.serverLoggerManager = new SimpleLoggerManager( System.err, "server" );
        this.serverLoggerManager.setRootLevel( SERVER_ROOT_LEVEL );
    }
    
    protected abstract T createServer() throws Exception;
    
    public void prepareServer() throws Exception {
        
    }
    
    public T getServer() {
        return this.server;
    }
                          

    @Before
    public void setUpClient() throws Exception {
        setUpClientLogger();
        InetSocketAddress address = new InetSocketAddress( "localhost", BasicStompServer.DEFAULT_PORT );
        this.client = new AbstractStompClient( address );
        this.client.setLoggerManager( this.clientLoggerManager );
    }

    public void setUpClientLogger() {
        this.clientLoggerManager = new SimpleLoggerManager( System.err, "client" );
        this.clientLoggerManager.setRootLevel( CLIENT_ROOT_LEVEL );
    }

    @After
    public void stopServer() throws Exception {
        this.server.stop();
    }
    
    public MessageAccumulator accumulator(String name, boolean shouldAck, boolean shouldNack) {
        MessageAccumulator accumulator = this.accumulators.get( name );
        if (accumulator == null) {
            accumulator = new MessageAccumulator( shouldAck, shouldNack );
            this.accumulators.put( name, accumulator );
        }

        return accumulator;
    }

    public MessageAccumulator accumulator(String name) {
        return accumulator( name, false, false );
    }

}
