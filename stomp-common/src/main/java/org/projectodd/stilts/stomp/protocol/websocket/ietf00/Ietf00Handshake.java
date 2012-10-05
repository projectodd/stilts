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

package org.projectodd.stilts.stomp.protocol.websocket.ietf00;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

import org.jboss.netty.buffer.ChannelBuffer;
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
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;

/**
 * Handler for ietf-00.
 * 
 * @author Michael Dobozy
 * @author Bob McWhirter
 * 
 */
public class Ietf00Handshake extends Handshake {


    public Ietf00Handshake() throws NoSuchAlgorithmException {
        super( "0" );
        this.challenge = new Ietf00WebSocketChallenge();
    }

    public boolean matches(HttpRequest request) {
        return (request.containsHeader( Names.SEC_WEBSOCKET_KEY1 ) && request.containsHeader( Names.SEC_WEBSOCKET_KEY2 ));
    }
    
    public HttpRequest generateRequest(URI uri) throws Exception {
        HttpRequest request = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toString() );

        request.addHeader( HttpHeaders.Names.CONNECTION, "Upgrade" );
        request.addHeader( HttpHeaders.Names.UPGRADE, "WebSocket" );
        request.addHeader( HttpHeaders.Names.HOST, uri.getHost()+ ":" + uri.getPort() );
        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, "stomp" );

        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1, this.challenge.getKey1String() );
        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2, this.challenge.getKey2String() );

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer( 6 );
        buffer.writeBytes( challenge.getKey3() );
        buffer.writeByte( '\r' );
        buffer.writeByte( '\n' );

        request.setContent( buffer );
        
        return request;
    }

    @Override
    public HttpResponse generateResponse(HttpRequest request) throws Exception {
        HttpResponse response = new DefaultHttpResponse( HttpVersion.HTTP_1_1, new HttpResponseStatus( 101, "Web Socket Protocol Handshake - IETF-00" ) );

        String origin = request.getHeader( Names.ORIGIN );

        if (origin != null) {
            response.addHeader( Names.SEC_WEBSOCKET_ORIGIN, request.getHeader( Names.ORIGIN ) );
        }
        response.addHeader( Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation( request ) );

        String protocol = request.getHeader( Names.SEC_WEBSOCKET_PROTOCOL );

        if (protocol != null) {
            response.addHeader( Names.SEC_WEBSOCKET_PROTOCOL, protocol );
        }

        // Calculate the answer of the challenge.
        String key1 = request.getHeader( Names.SEC_WEBSOCKET_KEY1 );
        String key2 = request.getHeader( Names.SEC_WEBSOCKET_KEY2 );
        byte[] key3 = new byte[8];
        request.getContent().readBytes( key3 );
        
        byte[] solution = Ietf00WebSocketChallenge.solve( key1, key2, key3 );
        
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer( solution.length + 2 );
        buffer.writeBytes( solution );

        response.setContent( buffer );
        response.setChunked( false );

        return response;
    }
    
    public boolean isComplete(HttpResponse response) throws Exception {
        ChannelBuffer content = response.getContent();
        
        byte[] challengeResponse = new byte[16];
        content.readBytes( challengeResponse );

        return this.challenge.verify( challengeResponse );
    }
    
    public ChannelHandler newEncoder() {
        return new Ietf00WebSocketFrameEncoder();
    }
    
    public ChannelHandler newDecoder() {
        return new Ietf00WebSocketFrameDecoder();
    }
    
    public ChannelHandler[] newAdditionalHandlers() {
        return EMPTY_HANDLER_ARRAY;
    }
    
    public int readResponseBody() {
        return 16;
    }
        
    private static final ChannelHandler[] EMPTY_HANDLER_ARRAY = new ChannelHandler[0];
    private Ietf00WebSocketChallenge challenge;
}
