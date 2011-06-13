package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.StompServer;

public class ConnectHandler extends AbstractControlFrameHandler {

    public ConnectHandler(StompServer server, ConnectionContext context) {
        super( server, context, Command.CONNECT );
        setRequiresClientIdentification( false );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            ClientAgent clientAgent = getServer().connect( new ChannelMessageSink( channelContext.getChannel() ), frame.getHeaders() );

            if (clientAgent != null) {
                StompFrame connected = StompFrames.newConnectedFrame( clientAgent.getSessionId() );
                sendFrame( channelContext, connected );
                channelContext.getChannel().write( connected );
            } else {
                sendErrorAndClose( channelContext, "Unable to connect" );
            }
        } catch (StompException e) {
           sendErrorAndClose( channelContext, e.getMessage() ); 
        }
    }

}
