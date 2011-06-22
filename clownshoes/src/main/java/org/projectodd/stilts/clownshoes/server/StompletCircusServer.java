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
import org.projectodd.stilts.clownshoes.stomplet.StompletMessageConduitFactory;

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
        PseudoXAMessageConduitFactory xaFactory = new PseudoXAMessageConduitFactory( factory );
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
