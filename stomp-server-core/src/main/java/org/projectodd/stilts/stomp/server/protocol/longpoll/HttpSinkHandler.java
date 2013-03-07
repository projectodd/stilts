package org.projectodd.stilts.stomp.server.protocol.longpoll;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
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
            HttpMessageSink sink = this.sinkManager.get( this.context.getConnectionContext() );
            sink.provideChannel( ctx.getChannel() );
        }
    }

    private WrappedConnectionContext context;
    private SinkManager sinkManager;

}
