package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.stomp.server.protocol.websockets.DisorderlyCloseEvent;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompDisorderlyCloseHandler extends AbstractProviderHandler {

    public StompDisorderlyCloseHandler(StompProvider provider, ConnectionContext context) {
        super( provider, context );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof DisorderlyCloseEvent) {
            getContext().getStompConnection().disconnect();
            ctx.getChannel().close();
        } else {
            super.handleUpstream( ctx, e );
        }
    }

}
