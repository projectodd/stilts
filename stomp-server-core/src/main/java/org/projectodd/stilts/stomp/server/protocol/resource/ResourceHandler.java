package org.projectodd.stilts.stomp.server.protocol.resource;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class ResourceHandler extends SimpleChannelUpstreamHandler {

    public ResourceHandler(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            String uri = httpReq.getUri();

            ChannelBuffer buffer = this.resourceManager.getResource( uri );
            if (buffer == null) {
                HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND );
                ctx.sendDownstream( new DownstreamMessageEvent( ctx.getChannel(), Channels.future( ctx.getChannel() ), httpResp, ctx.getChannel().getRemoteAddress() ) ); 
                return;
            }
            HttpResponse httpResp = new DefaultHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
            httpResp.setContent( buffer );

        }
        super.messageReceived( ctx, e );
    }

    private ResourceManager resourceManager;

}
