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

package org.projectodd.stilts.stomplet;

import org.projectodd.stilts.conduit.ConduitServer;
import org.projectodd.stilts.stomplet.conduit.StompletMessageConduitFactory;
import org.projectodd.stilts.stomplet.container.StompletContainer;


/** Virtual-hosting server supporting <code>StompletContainers</code>.
 * 
 * @author Bob McWhirter
 */
public class StompletServer extends ConduitServer<StompletMessageConduitFactory> {

    public StompletServer() {
        setMessageConduitFactory( new StompletMessageConduitFactory() );
    }

    public StompletServer(int port) {
        super( port );
        setMessageConduitFactory( new StompletMessageConduitFactory() );
    }

    public void registerVirtualHost(String host, StompletContainer container) {
        getMessageConduitFactory().registerVirtualHost( host, container );
    }

    public void setDefaultContainer(StompletContainer container) {
        getMessageConduitFactory().setDefaultContainer( container );
    }

}
