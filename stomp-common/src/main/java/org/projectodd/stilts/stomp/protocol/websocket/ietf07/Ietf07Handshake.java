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

package org.projectodd.stilts.stomp.protocol.websocket.ietf07;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;

/**
 * Handler for ietf-00.
 *
 * @author Michael Dobozy
 * @author Bob McWhirter
 *
 */
public class Ietf07Handshake extends Handshake {

    private static Logger log = Logger.getLogger(Ietf07Handshake.class);

    public Ietf07Handshake() throws NoSuchAlgorithmException {
        this( true );
    }

    public Ietf07Handshake(boolean isClient) throws NoSuchAlgorithmException {
        super( "7" );
        this.challenge = new Ietf07WebSocketChallenge();
        this.isClient = isClient;
    }

    public boolean matches(HttpRequest request) {
        return (request.containsHeader( "Sec-WebSocket-Key" ) && getVersion().equals( request.getHeader( "Sec-WebSocket-Version" ) ));
    }

    public HttpRequest generateRequest(URI uri) throws Exception {
        HttpRequest request = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath() );

        request.addHeader( "Sec-WebSocket-Version", "7" );
        request.addHeader( HttpHeaders.Names.CONNECTION, "Upgrade" );
        request.addHeader( HttpHeaders.Names.UPGRADE, "WebSocket" );
        request.addHeader( HttpHeaders.Names.HOST, uri.getHost()+ ":" + uri.getPort() );
        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, "stomp" );

        request.addHeader( "Sec-WebSocket-Key", this.challenge.getNonceBase64() );
        request.setContent( ChannelBuffers.EMPTY_BUFFER );

        return request;
    }

    @Override
    public HttpResponse generateResponse(HttpRequest request) throws Exception {
        HttpResponse response = new DefaultHttpResponse( HttpVersion.HTTP_1_1, new HttpResponseStatus( 101, "Web Socket Protocol Handshake - IETF-07" ) );

        String origin = request.getHeader( Names.ORIGIN );

        if (origin != null) {
            response.addHeader( Names.SEC_WEBSOCKET_ORIGIN, origin );
        }
        response.addHeader( Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation( request ) );

        String protocol = request.getHeader( Names.SEC_WEBSOCKET_PROTOCOL );

        if (protocol != null) {
            response.addHeader( Names.SEC_WEBSOCKET_PROTOCOL, protocol );
        }

        String key = request.getHeader( "Sec-WebSocket-Key" );
        String solution = Ietf07WebSocketChallenge.solve( key );

        response.addHeader( "Sec-WebSocket-Accept", solution );
        response.setChunked( false );

        return response;
    }

    @Override
    public boolean isComplete(HttpResponse response) throws Exception {
        log.errorf( "COMPLETE? " + response );
        String challengeResponse = response.getHeader( "Sec-WebSocket-Accept" );
        return this.challenge.verify( challengeResponse );
    }

    @Override
    public ChannelHandler newEncoder() {
        return new Ietf07WebSocketFrameEncoder( this.isClient );
    }

    @Override
    public ChannelHandler newDecoder() {
        return new Ietf07WebSocketFrameDecoder();
    }

    public ChannelHandler[] newAdditionalHandlers() {
        return new ChannelHandler[] {
                new PingHandler(),
        };
    }

    public int readResponseBody() {
        return 0;
    }

    private boolean isClient;
    private Ietf07WebSocketChallenge challenge;


}
