package org.jboss.stilts.circus.server;

import java.util.HashSet;

import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.jms.client.HornetQXAConnectionFactory;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;

public class HornetQCircusServer extends JMSCircusServer {

    public HornetQCircusServer() {
        super();
    }
    
    public HornetQCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Exception {
        startHornetQ();
        super.start();
    }
    
    protected void startHornetQ() throws Exception {
        Configuration config = getConfiguration();
        HornetQServer hornetQServer = HornetQServers.newHornetQServer( config ); 
        this.jmsManager = new JMSServerManagerImpl( hornetQServer );
        this.jmsManager.start();
        XAConnectionFactory cf = new HornetQXAConnectionFactory( false, new TransportConfiguration(InVMConnectorFactory.class.getName()));
        XAConnection connection = cf.createXAConnection();
        connection.start();
        setConnection( connection );
    }
    
    public void stop() throws Exception {
        super.stop();
        stopHornetQ();
    }
    
    protected void stopHornetQ() throws Exception {
        this.jmsManager.stop();
    }
    
    public void addQueue(String name) throws Exception {
        this.jmsManager.createQueue( false, name, null, false );
    }
    
    public void addTopic(String name) throws Exception {
        this.jmsManager.createTopic( false, name );
    }
    
    protected Configuration getConfiguration() {
        ConfigurationImpl config = new ConfigurationImpl();
        
        HashSet<TransportConfiguration> transports = new HashSet<TransportConfiguration>();
        transports.add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));
        config.setAcceptorConfigurations(transports);
        config.setPersistenceEnabled( false );
        config.setSecurityEnabled( false );
        return config;
    }

    private JMSServerManagerImpl jmsManager;


}
