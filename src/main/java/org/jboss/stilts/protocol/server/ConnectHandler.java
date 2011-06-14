package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.stilts.StompException;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Command;
import org.jboss.stilts.protocol.StompFrames;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.StompProvider;

public class ConnectHandler extends AbstractControlFrameHandler {

    public ConnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.CONNECT );
        setRequiresClientIdentification( false );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            ClientAgent clientAgent = getStompProvider().connect( new ChannelMessageSink( channelContext.getChannel() ), frame.getHeaders() );
            if (clientAgent != null) {
                getContext().setClientAgent( clientAgent );
                log.info( "Set client-agent: " + getClientAgent() );
                StompFrame connected = StompFrames.newConnectedFrame( clientAgent.getSessionId() );
                log.info( "Replying with CONNECTED" );
                sendFrame( channelContext, connected );
            } else {
                sendErrorAndClose( channelContext, "Unable to connect", frame );
            }
        } catch (StompException e) {
            log.error( "Error connecting", e );
            sendErrorAndClose( channelContext, e.getMessage(), frame );
        }
    }

}
