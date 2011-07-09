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

package org.projectodd.stilts.stomplet.conduit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.conduit.spi.MessageConduitFactory;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.stomplet.container.NoSuchHostException;
import org.projectodd.stilts.stomplet.container.StompletContainer;

public class StompletMessageConduitFactory implements MessageConduitFactory {

    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        String host = headers.get( Header.HOST );
        StompletContainer container = null;
        if (host == null) {
            container = this.defaultContainer;
            host = "default";
        } else {
            container = lookupVirtualHost( host );
        }

        if (container == null) {
            throw new NoSuchHostException( host );
        }

        return new StompletMessageConduit( container, messageSink );
    }

    public void registerVirtualHost(final String host, final StompletContainer container) {
        this.virtualHosts.put( host.toLowerCase(), container );
        if (this.defaultContainer == null) {
            this.defaultContainer = container;
        }
    }

    public StompletContainer lookupVirtualHost(final String host) {
        return this.virtualHosts.get( host.toLowerCase() );
    }

    public void setDefaultContainer(StompletContainer container) {
        this.defaultContainer = container;
    }

    public StompletContainer getDefaultContainer() {
        return this.defaultContainer;
    }

    public void start() throws Exception {
        Set<StompletContainer> containers = new HashSet<StompletContainer>();
        containers.addAll( this.virtualHosts.values() );
        if (this.defaultContainer != null) {
            containers.add( this.defaultContainer );
        }
        
        for ( StompletContainer each : containers ) {
            each.start();
        }
    }
    
    public void stop() throws Exception {
        Set<StompletContainer> containers = new HashSet<StompletContainer>();
        containers.addAll( this.virtualHosts.values() );
        if (this.defaultContainer != null) {
            containers.add( this.defaultContainer );
        }
        
        for ( StompletContainer each : containers ) {
            each.stop();
        }
    }

    private Map<String, StompletContainer> virtualHosts = new HashMap<String, StompletContainer>();
    private StompletContainer defaultContainer = null;


}
