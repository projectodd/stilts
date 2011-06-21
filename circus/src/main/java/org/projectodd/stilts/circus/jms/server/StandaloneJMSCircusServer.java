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
