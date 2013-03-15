package org.projectodd.stilts.stomp.server.protocol.resource;

import java.net.InetSocketAddress;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.server.protocol.HostDecodedEvent;

public class ResourceHandler extends SimpleChannelUpstreamHandler {

    public ResourceHandler(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof HostDecodedEvent) {
            this.host = ((HostDecodedEvent) e).getHost();
        }
        super.handleUpstream( ctx, e );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            String uri = httpReq.getUri();

            ChannelBuffer buffer = this.resourceManager.getResource( uri );
            if (buffer != null) {
                if (uri.equals( "/stomp.js" )) {
                    appendServerInformation( ctx, buffer );
                }
                HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
                httpResp.setContent( buffer );
                httpResp.setHeader( "Content-Length", "" + buffer.readableBytes() );
                httpResp.setHeader( "Content-Type", getContentType( uri ) );
                ctx.getChannel().write( httpResp );
                return;
            }
        }
        super.messageReceived( ctx, e );
    }

    private void appendServerInformation(ChannelHandlerContext ctx, ChannelBuffer buffer) {
        StringBuilder info = new StringBuilder();

        info.append( "\n" );
        if (this.host != null) {
            info.append( "Stomp.DEFAULT_HOST = '" + this.host + "';\n" );
        } else {
            info.append( "Stomp.DEFAULT_HOST = undefined;\n" );
        }
        int port = ((InetSocketAddress) ctx.getChannel().getLocalAddress()).getPort();
        info.append( "Stomp.DEFAULT_PORT = " ).append( port ).append( ";\n" );

        boolean secure = (ctx.getPipeline().get( SslHandler.class ) != null);

        info.append( "Stomp.DEFAULT_SECURE_FLAG = " + secure + ";\n" );

        if (this.host != null) {
            info.append( "WEB_SOCKET_SWF_LOCATION = 'http" );
            if (secure) {
                info.append( "s" );
            }
            info.append( "://" ).append( this.host ).append( ":" ).append( port ).append( "/WebSocketMain.swf';\n" );
        }

        buffer.writeBytes( info.toString().getBytes() );
    }

    protected String getContentType(String uri) {
        if (uri.endsWith( ".html" )) {
            return "text/html";
        }
        if (uri.endsWith( ".js" )) {
            return "text/javascript";
        }
        return "application/octet-stream";
    }

    private static Logger log = Logger.getLogger( ResourceHandler.class );

    private String host;
    private ResourceManager resourceManager;

}
