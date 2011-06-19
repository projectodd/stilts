package org.projectodd.stilts.protocol.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.protocol.StompFrame;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.protocol.StompFrames;
import org.projectodd.stilts.spi.StompConnection;
import org.projectodd.stilts.spi.StompProvider;

public class ConnectHandler extends AbstractControlFrameHandler {

    public ConnectHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.CONNECT );
        setRequiresClientIdentification( false );
    }

    @Override
    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {
            StompConnection clientAgent = getStompProvider().createConnection( new ChannelMessageSink( channelContext.getChannel(), getContext().getAckManager() ), frame.getHeaders() );
            if (clientAgent != null) {
                getContext().setStompConnection( clientAgent );
                log.info( "Set client-agent: " + getStompConnection() );
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
