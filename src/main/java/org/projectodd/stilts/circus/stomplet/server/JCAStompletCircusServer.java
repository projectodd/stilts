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

package org.projectodd.stilts.circus.stomplet.server;

import java.io.File;
import java.net.URL;

import javax.jms.XAConnectionFactory;
import javax.naming.InitialContext;

import org.jboss.jca.embedded.Embedded;
import org.jboss.jca.embedded.EmbeddedFactory;

public class JCAStompletCircusServer extends StompletCircusServer {

    public JCAStompletCircusServer() {
    }
    
    public JCAStompletCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startJCAContainer();
        super.start();
    }
    
    protected void startJCAContainer() throws Throwable {
        System.setProperty( "java.naming.factory.initial", "org.jnp.interfaces.LocalOnlyContextFactory" );
        System.setProperty( "java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" );
              
        this.jcaContainer = EmbeddedFactory.create();
        this.jcaContainer.startup();
        
        File raFile = new File( "ra/hornetq-ra.rar" );
        URL hornetQRaUrl = raFile.toURI().toURL();
        this.jcaContainer.deploy( hornetQRaUrl );
        
        //Object result = this.jcaContainer.lookup( "hornetq-ra", Object.class); 
        //System.err.println( "Connection Factory: " + result );


        InitialContext context = new InitialContext();
        Object o = context.lookup( "java:/eis/hornetq-ra" );
        System.err.println( "Connection Factory: " + o );
        
        Object tm = this.jcaContainer.lookup( "RealTransactionManager", Object.class );
        System.err.println( "TM: " + tm );

    }
    
    public void stop() throws Throwable {
        super.stop();
        stopJCAContainer();
    }
    
    protected void stopJCAContainer() throws Throwable {
        this.jcaContainer.shutdown();
    }
    
    private Embedded jcaContainer;

}
