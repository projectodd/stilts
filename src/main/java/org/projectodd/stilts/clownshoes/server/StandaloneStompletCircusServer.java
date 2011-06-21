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

package org.projectodd.stilts.clownshoes.server;

import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.server.StandaloneCircusServer;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.circus.xa.psuedo.PsuedoXAMessageConduitFactory;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.StompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.StompletMessageConduitFactory;

public class StandaloneStompletCircusServer extends StandaloneCircusServer<StompletCircusServer> {

    public StandaloneStompletCircusServer(StompletCircusServer server) {
        super( server );
    }

    public void configure() throws Throwable {
        super.configure();
        SimpleStompletContainer stompletContainer = new SimpleStompletContainer( );
        MessageConduitFactory conduitFactory = new StompletMessageConduitFactory( stompletContainer );
        XAMessageConduitFactory xaConduitFactory = new PsuedoXAMessageConduitFactory( conduitFactory );
        getServer().setStompletContainer( stompletContainer );
        getServer().setMessageConduitFactory( xaConduitFactory );
    }
    
    public StompletContainer getStompletContainer() {
        return getServer().getStompletContainer();
    }
    
    public void stop() throws Throwable {
        super.stop();
    }

}
