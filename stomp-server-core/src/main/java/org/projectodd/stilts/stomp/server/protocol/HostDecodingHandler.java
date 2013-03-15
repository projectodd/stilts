package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;

public class HostDecodingHandler implements ChannelUpstreamHandler {

    public HostDecodingHandler() {
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {

            if (((MessageEvent) e).getMessage() instanceof HttpRequest) {
                HttpRequest httpReq = (HttpRequest) ((MessageEvent) e).getMessage();
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
            }

        }
        ctx.sendUpstream( e );
    }

    private static Logger log = Logger.getLogger( HostDecodingHandler.class );

}
