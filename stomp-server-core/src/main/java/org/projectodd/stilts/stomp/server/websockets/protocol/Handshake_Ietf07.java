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

package org.projectodd.stilts.stomp.server.websockets.protocol;

import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketChallenge_Ietf07;

/**
 * Handler for ietf-00.
 * 
 * @author Michael Dobozy
 * @author Bob McWhirter
 * 
 */
public class Handshake_Ietf07 extends Handshake {

    public Handshake_Ietf07() {
        super( "7" );
    }

    public boolean matches(HttpRequest request) {
        return (request.containsHeader( "Sec-WebSocket-Key" ) && getVersion().equals( request.getHeader( "Sec-WebSocket-Version" ) ));
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
        String solution = WebSocketChallenge_Ietf07.solve( key );

        response.addHeader( "Sec-WebSocket-Accept", solution );
        response.setChunked( false );

        return response;
    }
}