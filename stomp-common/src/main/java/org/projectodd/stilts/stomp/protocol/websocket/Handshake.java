/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
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

package org.projectodd.stilts.stomp.protocol.websocket;

import java.net.URI;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * Abstraction of web-socket handshake versions.
 * 
 * <p>
 * Since each version uses different headers and behaves differently, these
 * differences are encapsulated in subclasses of <code>Handshake</code>.
 * </p>
 * 
 * @see HandshakeHandler
 * 
 * @author Bob McWhirter
 */
public abstract class Handshake {

    public Handshake(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    protected String getWebSocketLocation(HttpRequest request) {
        return "ws://" + request.getHeader( HttpHeaders.Names.HOST ) + request.getUri();
    }

    public abstract boolean matches(HttpRequest request);

    public abstract HttpRequest generateRequest(URI uri) throws Exception;
    public abstract HttpResponse generateResponse(HttpRequest request) throws Exception;
    public abstract boolean isComplete(HttpResponse response) throws Exception;
    
    public abstract ChannelHandler newEncoder();
    public abstract ChannelHandler newDecoder();
    public abstract ChannelHandler[] newAdditionalHandlers();
    
    public abstract int readResponseBody();

    private String version;

}
