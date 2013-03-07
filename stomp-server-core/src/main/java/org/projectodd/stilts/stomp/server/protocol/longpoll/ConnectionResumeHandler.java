package org.projectodd.stilts.stomp.server.protocol.longpoll;

import java.util.Random;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.projectodd.stilts.stomp.server.protocol.ConnectionContext;
import org.projectodd.stilts.stomp.server.protocol.DefaultConnectionContext;
import org.projectodd.stilts.stomp.server.protocol.HostDecodedEvent;
import org.projectodd.stilts.stomp.server.protocol.WrappedConnectionContext;
import org.projectodd.stilts.stomp.server.protocol.websockets.SessionDecodedEvent;

public class ConnectionResumeHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public ConnectionResumeHandler(ConnectionManager connectionManager, WrappedConnectionContext context) {
        this.connectionManager = connectionManager;
        this.context = context;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            log.debug( "RESUME: " + e );
            if (((MessageEvent) e).getMessage() instanceof HttpRequest) {

                CookieDecoder cookieDecoder = new CookieDecoder();
                ConnectionContext connectionContext = null;

                HttpRequest httpReq = (HttpRequest) ((MessageEvent) e).getMessage();
                String cookieHeader = httpReq.getHeader( "Cookie" );
                if (cookieHeader != null) {
                    Set<Cookie> cookies = cookieDecoder.decode( cookieHeader );
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals( "stomp-connection-id" )) {
                            this.connectionId = cookie.getValue();
                            log.debugf( "reconnect to connection: %s", connectionId );
                            connectionContext = this.connectionManager.get( connectionId );
                            log.debugf( "reconnected to connection: %s", connectionContext );
                            break;
                        }
                    }
                }

                boolean newConnection = false;
                if (connectionContext == null) {
                    connectionContext = new DefaultConnectionContext();
                    this.connectionId = createConnectionId( connectionContext );
                    log.debugf( "stash connection %s as %s", connectionContext, connectionId );
                    this.connectionManager.put( connectionId, connectionContext );
                    newConnection = true;
                }

                this.context.setConnectionContext( connectionContext );

                if (newConnection) {
                    String hostPort = httpReq.getHeader( HttpHeaders.Names.HOST );

                    if (hostPort != null) {
                        int colonLoc = hostPort.indexOf( ':' );
                        String host = hostPort;
                        if (colonLoc > 0) {
                            host = hostPort.substring( 0, colonLoc );
                        }

                        ChannelEvent hostDecodedEvent = new HostDecodedEvent( ctx.getChannel(), host );
                        ctx.sendUpstream( hostDecodedEvent );
                    }

                    if (cookieHeader != null) {
                        Set<Cookie> cookies = cookieDecoder.decode( cookieHeader );

                        for (Cookie each : cookies) {
                            if (each.getName().equalsIgnoreCase( "jsessionid" )) {
                                ChannelEvent sessionDecodedEvent = new SessionDecodedEvent( ctx.getChannel(), each.getValue() );
                                ctx.sendUpstream( sessionDecodedEvent );
                                break;
                            }
                        }
                    }
                }
            }
        }
        ctx.sendUpstream( e );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            if (((MessageEvent) e).getMessage() instanceof HttpResponse) {
                HttpResponse httpResp = (HttpResponse) ((MessageEvent) e).getMessage();

                CookieEncoder cookieEncoder = new CookieEncoder( true );
                String cookieHeader = httpResp.getHeader( "Set-Cookie" );
                if (cookieHeader != null) {
                    CookieDecoder cookieDecoder = new CookieDecoder();
                    Set<Cookie> cookies = cookieDecoder.decode( cookieHeader );

                    for (Cookie each : cookies) {
                        cookieEncoder.addCookie( each );
                    }
                }
                Cookie connectionCookie = new DefaultCookie( "stomp-connection-id", this.connectionId );
                cookieEncoder.addCookie( connectionCookie );
                httpResp.setHeader( "Set-Cookie", cookieEncoder.encode() );
            }
        }

        ctx.sendDownstream( e );
    }

    protected String createConnectionId(ConnectionContext connectionContext) {
        return Long.toHexString( new Random( System.identityHashCode( connectionContext ) ).nextLong() );
    }

    private static Logger log = Logger.getLogger( ConnectionResumeHandler.class );

    private ConnectionManager connectionManager;
    private WrappedConnectionContext context;
    private String connectionId;

}
