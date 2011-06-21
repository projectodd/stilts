/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
