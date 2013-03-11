package org.projectodd.stilts.stomp.server.protocol.longpoll;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.projectodd.stilts.stomp.server.protocol.WrappedConnectionContext;

public class HttpSinkHandler extends SimpleChannelUpstreamHandler {

    public HttpSinkHandler(WrappedConnectionContext context, SinkManager sinkManager) {
        this.context = context;
        this.sinkManager = sinkManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            if (httpReq.getMethod().equals( HttpMethod.GET ) && "text/stomp-poll".equals( httpReq.getHeader( "Content-Type" ) )) {
                log.debug( "Hooking up the sink" );
                HttpMessageSink sink = this.sinkManager.get( this.context.getConnectionContext() );
                sink.provideChannel( ctx.getChannel() );
                this.provided = true;
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

    private static Logger log = Logger.getLogger( HttpSinkHandler.class );

    private boolean provided = false;
    private WrappedConnectionContext context;
    private SinkManager sinkManager;

}
