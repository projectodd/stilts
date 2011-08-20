package org.projectodd.stilts.stomp.server.websockets.protocol;

import java.io.IOException;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class DisorderlyCloseHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        Throwable cause = e.getCause();

        if (cause instanceof IOException ) {
            if (ctx.getAttachment() == null) {
                ctx.setAttachment( Boolean.TRUE );
                ctx.sendUpstream( new DisorderlyCloseEvent( ctx.getChannel() ) );
                ctx.getChannel().disconnect();
            }
        } else {
            ctx.sendUpstream( e );
        }
    }

}
