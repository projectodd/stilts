package org.projectodd.stilts.stomp.server.protocol.http;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.projectodd.stilts.stomp.server.protocol.WrappedConnectionContext;

public class SSESinkHandler extends SimpleChannelUpstreamHandler {

    public SSESinkHandler(WrappedConnectionContext context, SinkManager sinkManager) {
        this.context = context;
        this.sinkManager = sinkManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            log.debug( "method: " + httpReq.getMethod() );
            log.debug( "content-type: " + httpReq.getHeader( "Content-Type" ) );
            if (httpReq.getMethod().equals( HttpMethod.GET ) && "text/event-stream".equals( httpReq.getHeader( "Accept" ) )) {
                HttpMessageSink sink = this.sinkManager.get( this.context.getConnectionContext() );
                sink.provideChannel( ctx.getChannel(), false );
                this.provided = true;
                HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
                httpResp.setHeader( "Content-Type", "text/event-stream" );
                ctx.getChannel().write( httpResp );
            }
        }
        if (!this.provided) {
            log.debugf( "NOT Hooking up the sink %s", e );
        }
        super.messageReceived( ctx, e );
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        if (this.provided) {
            HttpMessageSink sink = this.sinkManager.get( this.context.getConnectionContext() );
            sink.clearChannel();
        }
    }

    private static Logger log = Logger.getLogger( SSESinkHandler.class );

    private boolean provided = false;
    private WrappedConnectionContext context;
    private SinkManager sinkManager;

}
