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
