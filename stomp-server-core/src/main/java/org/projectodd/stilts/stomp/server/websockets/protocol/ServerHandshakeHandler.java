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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.UpstreamChannelStateEvent;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketDisconnectionNegotiator;
import org.projectodd.stilts.stomp.protocol.websocket.ietf00.Ietf00Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf07.Ietf07Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf17.Ietf17Handshake;
import org.projectodd.stilts.stomp.server.protocol.HostDecodedEvent;

/**
 * Multi-verison handshake handler for the web-sockets protocol family.
 * 
 * @see Handshake
 * 
 * @author Trustin Lee
 * @author Michael Dobozy
 * @author Bob McWhirter
 */
public class ServerHandshakeHandler extends SimpleChannelUpstreamHandler {

    /**
     * Construct.
     * 
     * @param contextRegistry The context registry.
     * @throws NoSuchAlgorithmException 
     */
    public ServerHandshakeHandler() throws NoSuchAlgorithmException {
        this.handshakes.add( new Ietf17Handshake( false ) );
        this.handshakes.add( new Ietf07Handshake( false ) );
        this.handshakes.add( new Ietf00Handshake() );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object msg = e.getMessage();
        if (msg instanceof HttpRequest) {
            handleHttpRequest( ctx, (HttpRequest) msg );
        } else {
            super.messageReceived( ctx, e );
        }
    }

    /**
     * Handle initial HTTP portion of the handshake.
     * 
     * @param channelContext
     * @param request
     * @throws Exception
     */
    protected void handleHttpRequest(final ChannelHandlerContext channelContext, final HttpRequest request) throws Exception {
        if (isWebSocketsUpgradeRequest( request )) {

            final Handshake handshake = findHandshake( request );

            if (handshake != null) {

                HttpResponse response = handshake.generateResponse( request );

                response.addHeader( Names.UPGRADE, Values.WEBSOCKET );
                response.addHeader( Names.CONNECTION, Values.UPGRADE );

                final ChannelPipeline pipeline = channelContext.getChannel().getPipeline();
                reconfigureUpstream( pipeline, handshake );

                Channel channel = channelContext.getChannel();
                ChannelFuture future = channel.write( response );
                future.addListener( new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        reconfigureDownstream( pipeline, handshake );
                        pipeline.replace( ServerHandshakeHandler.this, "websocket-disconnection-negotiator", new WebSocketDisconnectionNegotiator() );
                        forwardConnectEventUpstream( channelContext );
                        decodeHost( channelContext, request );
                        decodeSession( channelContext, request );
                    }
                } );

                return;
            }
        }

        // Send an error page otherwise.
        sendHttpResponse( channelContext, request, new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN ) );
    }

    protected void forwardConnectEventUpstream(ChannelHandlerContext channelContext) {
        ChannelEvent connectEvent = new UpstreamChannelStateEvent( channelContext.getChannel(), ChannelState.CONNECTED, channelContext.getChannel().getRemoteAddress() );
        channelContext.sendUpstream( connectEvent );
    }

    protected void decodeHost(ChannelHandlerContext channelContext, HttpRequest request) {
        String hostPort = request.getHeader( HttpHeaders.Names.HOST );

        if (hostPort != null) {
            int colonLoc = hostPort.indexOf( ':' );
            String host = hostPort;
            if (colonLoc > 0) {
                host = hostPort.substring( 0, colonLoc );
            }

            ChannelEvent hostDecodedEvent = new HostDecodedEvent( channelContext.getChannel(), host );
            channelContext.sendUpstream( hostDecodedEvent );
        }
    }

    protected void decodeSession(ChannelHandlerContext channelContext, HttpRequest request) {
        CookieDecoder decoder = new CookieDecoder();

        String cookieHeader = request.getHeader( HttpHeaders.Names.COOKIE );

        if (cookieHeader == null || cookieHeader.trim().equals( "" )) {
            return;
        }

        Set<Cookie> cookies = decoder.decode( cookieHeader );

        for (Cookie each : cookies) {
            if (each.getName().equalsIgnoreCase( "jsessionid" )) {
                ChannelEvent sessionDecodedEvent = new SessionDecodedEvent( channelContext.getChannel(), each.getValue() );
                channelContext.sendUpstream( sessionDecodedEvent );
                break;
            }
        }
    }

    /**
     * Locate a matching handshake version.
     * 
     * @param request The HTTP request.
     * @return The matching handshake, otherwise <code>null</code> if none
     *         match.
     */
    protected Handshake findHandshake(HttpRequest request) {
        for (Handshake handshake : this.handshakes) {
            if (handshake.matches( request )) {
                return handshake;
            }
        }

        return null;
    }

    /**
     * Remove HTTP handlers, replace with web-socket handlers.
     * 
     * @param pipeline The pipeline to reconfigure.
     */
    protected void reconfigureUpstream(ChannelPipeline pipeline, Handshake handshake) {
        pipeline.replace( "http-decoder", "websockets-decoder", handshake.newDecoder() );
        ChannelHandler[] additionalHandlers = handshake.newAdditionalHandlers();
        String currentTail = "websockets-decoder";
        for ( ChannelHandler each : additionalHandlers ) {
            String handlerName = "additional-" + each.getClass().getSimpleName();
            pipeline.addAfter( currentTail, handlerName, each); 
            currentTail = handlerName;
        }
    }

    /**
     * Remove HTTP handlers, replace with web-socket handlers
     * 
     * @param pipeline The pipeline to reconfigure.
     */
    protected void reconfigureDownstream(ChannelPipeline pipeline, Handshake handshake) {
        pipeline.replace( "http-encoder", "websockets-encoder", handshake.newEncoder() );
    }

    /**
     * Determine if this request represents a web-socket upgrade request.
     * 
     * @param request The request to inspect.
     * @return <code>true</code> if this request is indeed a web-socket upgrade
     *         request, otherwise <code>false</code>.
     */
    protected boolean isWebSocketsUpgradeRequest(HttpRequest request) {
        String connectionHeader = request.getHeader( Names.CONNECTION );
        String upgradeHeader = request.getHeader( Names.UPGRADE );

        if (connectionHeader == null || upgradeHeader == null) {
            return false;
        }

        if (connectionHeader.trim().toLowerCase().contains( Values.UPGRADE.toLowerCase() )) {
            if (upgradeHeader.trim().equalsIgnoreCase( Values.WEBSOCKET )) {
                return true;
            }
        }

        return false;
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        // Generate an error page if response status code is not OK (200).
        if (res.getStatus().getCode() != 200) {
            res.setContent(
                    ChannelBuffers.copiedBuffer(
                            res.getStatus().toString(), CharsetUtil.UTF_8 ) );
            HttpHeaders.setContentLength( res, res.getContent().readableBytes() );
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.getChannel().write( res );
        if (!HttpHeaders.isKeepAlive( req ) || res.getStatus().getCode() != 200) {
            f.addListener( ChannelFutureListener.CLOSE );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    private static final Logger log = Logger.getLogger( "org.torquebox.web.websockets.protocol" );
    private List<Handshake> handshakes = new ArrayList<Handshake>();

}
