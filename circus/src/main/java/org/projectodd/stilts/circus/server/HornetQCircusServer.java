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
import org.projectodd.stilts.circus.jms.server.JMSCircusServer;

public class HornetQCircusServer extends JMSCircusServer {

    public HornetQCircusServer() {
        super();
    }
    
    public HornetQCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
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
    
    public void stop() throws Throwable {
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
