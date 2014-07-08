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

package org.projectodd.stilts.stomp.client;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.client.protocol.ClientContext;
import org.projectodd.stilts.stomp.client.protocol.ClientMessageHandler;
import org.projectodd.stilts.stomp.client.protocol.ClientReceiptHandler;
import org.projectodd.stilts.stomp.client.protocol.StompConnectionNegotiator;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketConnectionNegotiator;
import org.projectodd.stilts.stomp.client.protocol.websockets.WebSocketHttpResponseDecoder;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.protocol.StompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.StompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketStompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.websocket.ietf00.Ietf00Handshake;

public class StompClientPipelineFactory implements ChannelPipelineFactory {

    public StompClientPipelineFactory(StompClient client, ClientContext clientContext) {
        this( client, clientContext, null );
    }

    public StompClientPipelineFactory(StompClient client, ClientContext clientContext, boolean useWebSockets) throws NoSuchAlgorithmException {
        this( client, clientContext, useWebSockets ? new Ietf00Handshake() : null );
    }

    public StompClientPipelineFactory(StompClient client, ClientContext clientContext, Handshake handshake) {
        this.client = client;
        this.clientContext = clientContext;
        this.handshake = handshake;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast( "debug-client-head", new DebugHandler( "CLIENT_HEAD" ) );
        if (this.clientContext.isSecure()) {
            SSLEngine sslEngine = this.clientContext.getSSLContext().createSSLEngine();
            sslEngine.setUseClientMode( true );
            SslHandler sslHandler = new SslHandler( sslEngine );
            sslHandler.setEnableRenegotiation( false );
            sslHandler.setIssueHandshake( true );
            pipeline.addLast( "ssl", sslHandler );
            pipeline.addLast( "client-post-ssl", new DebugHandler( "SERVER-POST-SSL" ) );
        }
        if (this.handshake != null) {
            pipeline.addLast( "http-encoder", new HttpRequestEncoder() );
            pipeline.addLast( "http-decoder", new WebSocketHttpResponseDecoder( this.handshake ) );
            pipeline.addLast( "websocket-connection-negotiator", new WebSocketConnectionNegotiator( this.clientContext.getWebSocketAddress(), this.handshake ) );
            pipeline.addLast( "stomp-frame-decoder", new WebSocketStompFrameDecoder() );
            pipeline.addLast( "stomp-frame-encoder", new WebSocketStompFrameEncoder() );
        } else {
            pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder() );
            pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder() );
        }

        pipeline.addLast( "debug-client-mid", new DebugHandler( "CLIENT_MID" ) );

        pipeline.addLast( "stomp-connection-negotiator", new StompConnectionNegotiator( clientContext, "localhost" ) );
        pipeline.addLast( "stomp-client-receipt", new ClientReceiptHandler( clientContext ) );

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder() );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( new ClientStompMessageFactory( this.client ) ) );

        pipeline.addLast( "stomp-client-message-handler", new ClientMessageHandler( clientContext ) );
        pipeline.addLast( "debug-client-tail", new DebugHandler( "CLIENT_TAIL" ) );

        return pipeline;
    }

    private StompClient client;
    private ClientContext clientContext;
    private Handshake handshake;

}
