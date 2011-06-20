package org.projectodd.stilts.circus.jms.server;

import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.naming.InitialContext;

import org.projectodd.stilts.circus.server.StandaloneCircusServer;

public class StandaloneJMSCircusServer extends StandaloneCircusServer<JMSCircusServer> {


    public StandaloneJMSCircusServer(JMSCircusServer server) {
        super( server );
    }
    
    public void configure() throws Throwable {
        super.configure();
        
        InitialContext context = new InitialContext();
        XAConnectionFactory connectionFactory = (XAConnectionFactory) context.lookup( "java:/eis/hornetq-ra" );
        this.connection = connectionFactory.createXAConnection();
        getServer().setConnection( this.connection );
    }
    
    public void stop() throws Throwable {
        this.connection.close();
        this.connection = null;
        super.stop();
    }

    private XAConnection connection;
}
