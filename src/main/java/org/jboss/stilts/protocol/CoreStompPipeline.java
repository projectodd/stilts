package org.jboss.stilts.protocol;

import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.stilts.protocol.server.ConnectHandler;
import org.jboss.stilts.protocol.server.ConnectionContext;
import org.jboss.stilts.protocol.server.DisconnectHandler;
import org.jboss.stilts.protocol.server.ReceiptHandler;
import org.jboss.stilts.protocol.server.SubscribeHandler;
import org.jboss.stilts.protocol.server.UnsubscribeHandler;
import org.jboss.stilts.spi.StompServer;

public class CoreStompPipeline extends DefaultChannelPipeline {

    public CoreStompPipeline(StompServer server) {
        ConnectionContext context = new ConnectionContext();
        
        addLast( "stomp-message-decoder", new StompMessageDecoder() );
        addLast( "stomp-message-encoder", new StompServerMessageEncoder() );
        
        addLast( "stomp-server-connect", new ConnectHandler( server, context ) );
        addLast( "stomp-server-disconnect", new DisconnectHandler( server, context ) );
        
        addLast( "stomp-server-subscribe", new SubscribeHandler( server, context ) );
        addLast( "stomp-server-unsubscribe", new UnsubscribeHandler( server, context ) );
        
        addLast( "stomp-server-receipt", new ReceiptHandler( server, context ) );
        
    }

}
