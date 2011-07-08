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

package org.projectodd.stilts.clownshoes.server;

import org.projectodd.stilts.circus.server.CircusServer;
import org.projectodd.stilts.circus.xa.pseudo.PseudoXAMessageConduitFactory;
import org.projectodd.stilts.clownshoes.stomplet.StompletContainer;
import org.projectodd.stilts.clownshoes.stomplet.VirtualHostingStompletMessageConduitFactory;

public class VirtualHostingStompletCircusServer extends CircusServer {

    public VirtualHostingStompletCircusServer() {
        this.factory = new VirtualHostingStompletMessageConduitFactory();
    }
    
    public VirtualHostingStompletCircusServer(int port) {
        super( port );
        this.factory = new VirtualHostingStompletMessageConduitFactory();
    }
    
    public void registerVirtualHost(String host, StompletContainer container) {
        this.factory.registerVirtualHost( host, container );
    }
    
    public void setDefaultContainer(StompletContainer container) {
        this.factory.setDefaultContainer( container );
    }
    
    public void start() throws Throwable {
        startConduitFactory();
        super.start();
    }
    
    public void stop() throws Throwable {
        super.stop();
        stopConduitFactory();
    }
    
    protected void startConduitFactory() throws Exception {
        this.factory.start();
        PseudoXAMessageConduitFactory xaFactory = new PseudoXAMessageConduitFactory( factory );
        setMessageConduitFactory( xaFactory );
    }
    
    protected void stopConduitFactory() throws Exception {
        this.factory.stop();
    }

    private VirtualHostingStompletMessageConduitFactory factory;

}
