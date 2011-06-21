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

import org.projectodd.stilts.circus.jms.DestinationMapper;
import org.projectodd.stilts.circus.jms.JMSMessageConduitFactory;
import org.projectodd.stilts.circus.server.CircusServer;

public class JMSCircusServer extends CircusServer {

    public JMSCircusServer() {
        super();
    }
    
    public JMSCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startConduitFactory();
        super.start();
    }
    
    protected void startConduitFactory() {
        JMSMessageConduitFactory factory = new JMSMessageConduitFactory( getConnection(), getDestinationMapper() );
        setMessageConduitFactory( factory );
    }
    
    public void setConnection(XAConnection connection) {
        this.connection = connection;
    }
    
    public XAConnection getConnection() {
        return this.connection;
    }
    
    public void setDestinationMapper(DestinationMapper destinationMapper) {
        this.destinationMapper = destinationMapper;
    }
    
    public DestinationMapper getDestinationMapper() {
        return this.destinationMapper;
    }

    private DestinationMapper destinationMapper;
    private XAConnection connection;

}
