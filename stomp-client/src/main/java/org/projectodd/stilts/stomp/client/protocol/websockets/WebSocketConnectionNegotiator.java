package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.net.URI;
import java.security.NoSuchAlgorithmException;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.projectodd.stilts.stomp.protocol.websocket.Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketDisconnectionNegotiator;

/** WebSockets protocol connection negotiator.
 * 
 * <p>This handler reacts to Netty's CONNECTED event and handles the handshake
 * of the WebSockets HTTP upgrade handshake.  Upon successful completion, it forwards
 * a CONNECTED event upstream to the underlying protocol making use of the websocket
 * transport. For instance, STOMP.</p>
 * 
 * @author Bob McWhirter
 */
public class WebSocketConnectionNegotiator extends SimpleChannelUpstreamHandler {
    

    public WebSocketConnectionNegotiator(String host, int port, Handshake handshake) throws NoSuchAlgorithmException {
        this.host = host;
        this.port = port;
        this.handshake = handshake;
    }

    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent e) throws Exception {
        log.info( "Starting websockets connection " + e.getClass() );
        URI uri = new URI( "ws://" + this.host + ":" + this.port + "/" );
        HttpRequest request = this.handshake.generateRequest( uri );
        this.connectedEvent = e;
        Channel channel = context.getChannel();
        Channels.write( channel, request );
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) e.getMessage();
            
            if ( this.handshake.isComplete( response) ) {
                ChannelPipeline pipeline = context.getPipeline();
                if (pipeline.get( WebSocketHttpResponseDecoder.class ) != null) {
                    pipeline.replace( WebSocketHttpResponseDecoder.class, "websockets-decoder", this.handshake.newDecoder() );
                } else {
                    pipeline.addFirst( "websockets-decoder", this.handshake.newDecoder() );
                }
                if (pipeline.get( HttpRequestEncoder.class ) != null) {
                    pipeline.replace( HttpRequestEncoder.class, "websockets-encoder", this.handshake.newEncoder() );
                } else {
                    pipeline.addAfter( "websockets-decoder", "websockets-encoder", this.handshake.newEncoder() );
                }
                
                ChannelHandler[] additionalHandlers = this.handshake.newAdditionalHandlers();
                String currentTail = "websockets-decoder";
                for ( ChannelHandler each : additionalHandlers ) {
                    String handlerName = "additional-" + each.getClass().getSimpleName();
                    pipeline.addAfter( currentTail, handlerName, each); 
                    currentTail = handlerName;
                }
                
                context.sendUpstream( this.connectedEvent );
                pipeline.replace( this, "websocket-disconnection-negotiator", new WebSocketDisconnectionNegotiator() );
            }
        } else {
            super.messageReceived( context, e );
        }
    }

    private static final Logger log = Logger.getLogger( "stomp.proto.client.websocket" );
    private String host;
    private int port;
    private Handshake handshake;
    private ChannelStateEvent connectedEvent;

}
