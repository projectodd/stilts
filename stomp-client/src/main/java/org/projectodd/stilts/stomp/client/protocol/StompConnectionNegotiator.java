package org.projectodd.stilts.stomp.client.protocol;

import java.security.NoSuchAlgorithmException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.UpstreamChannelStateEvent;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;

/** Base STOMP protocol connection negotiator.
 * 
 * <p>This handler reacts to Netty's CONNECTED event and handles the handshake
 * of the STOMP CONNECT and CONNECTED interaction.</p>
 * 
 * @author Bob McWhirter
 */
public class StompConnectionNegotiator extends AbstractClientControlFrameHandler {

    public StompConnectionNegotiator(ClientContext clientContext, String host) throws NoSuchAlgorithmException {
        super( clientContext, Command.CONNECTED );
        this.host = host;
    }

    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent e) throws Exception {
        StompControlFrame frame = new StompControlFrame( Command.CONNECT );
        frame.setHeader( Header.HOST, this.host );
        frame.setHeader( Header.ACCEPT_VERSION, Version.supportedVersions() );

        Channels.write( context.getChannel(), frame );
    }

    @Override
    protected void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        getClientContext().setConnectionState( State.CONNECTED );
        String version = frame.getHeader( Header.VERSION );
        if (version != null) {
            getClientContext().setVersion( Version.forVersionString( version ) );
        }
        
        Channel channel = channelContext.getChannel();
        
        channelContext.sendUpstream( new UpstreamChannelStateEvent( channel, ChannelState.CONNECTED, channel.getRemoteAddress() ) );
    }

    private String host;

}
