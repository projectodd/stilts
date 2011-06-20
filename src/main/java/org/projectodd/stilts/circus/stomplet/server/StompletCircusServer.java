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

import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.circus.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.circus.stomplet.StompletContainer;
import org.projectodd.stilts.circus.stomplet.StompletMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;

public class StompletCircusServer extends CircusServer {

    public StompletCircusServer() {
    }
    
    public StompletCircusServer(int port) {
        super( port );
    }
    
    public void start() throws Throwable {
        startConduitFactory();
        this.stompletContainer.start();
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
        this.stompletContainer.stop();
    }
    
    protected void startConduitFactory() {
        StompletMessageConduitFactory factory = new StompletMessageConduitFactory( this.stompletContainer );
        PsuedoXAMessageConduitFactory xaFactory = new PsuedoXAMessageConduitFactory( factory );
        setMessageConduitFactory( xaFactory );
    }

    public void setStompletContainer(StompletContainer stompletContainer) {
        this.stompletContainer = stompletContainer;
    }
    
    public StompletContainer getStompletContainer() {
        return this.stompletContainer;
    }

    private StompletContainer stompletContainer;

}
