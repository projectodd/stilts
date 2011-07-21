package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.security.NoSuchAlgorithmException;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketChallenge;
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
    

    public WebSocketConnectionNegotiator(String host, int port) throws NoSuchAlgorithmException {
        this.host = host;
        this.port = port;
        this.challenge = new WebSocketChallenge();
    }

    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent e) throws Exception {
        log.info( "Starting websockets connection " + e.getClass() );
        String url = "stomp+ws://" + this.host + ":" + this.port + "/";

        HttpRequest request = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, url );

        request.addHeader( HttpHeaders.Names.CONNECTION, "Upgrade" );
        request.addHeader( HttpHeaders.Names.UPGRADE, "WebSocket" );
        request.addHeader( HttpHeaders.Names.HOST, this.host + ":" + this.port );
        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, "stomp" );

        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1, this.challenge.getKey1String() );
        request.addHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2, this.challenge.getKey2String() );

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer( 6 );
        buffer.writeBytes( this.challenge.getKey3() );
        buffer.writeByte( '\r' );
        buffer.writeByte( '\n' );

        request.setContent( buffer );

        Channel channel = context.getChannel();

        this.connectedEvent = e;
        Channels.write( channel, request );
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) e.getMessage();

            ChannelBuffer content = response.getContent();

            
            System.err.println( "READABLE: " + content.readableBytes() );
            byte[] challengeResponse = new byte[16];
            content.readBytes( challengeResponse );

            if (this.challenge.verify( challengeResponse )) {
                ChannelPipeline pipeline = context.getPipeline();
                if (pipeline.get( WebSocketHttpResponseDecoder.class ) != null) {
                    pipeline.replace( WebSocketHttpResponseDecoder.class, "websockets-decoder", new WebSocketFrameDecoder() );
                } else {
                    pipeline.addFirst( "websockets-decoder", new WebSocketFrameDecoder() );
                }

                if (pipeline.get( HttpRequestEncoder.class ) != null) {
                    pipeline.replace( HttpRequestEncoder.class, "websockets-encoder", new WebSocketFrameEncoder() );
                } else {
                    pipeline.addAfter( "websockets-decoder", "websockets-encoder", new WebSocketFrameEncoder() );
                }
                pipeline.addBefore( "websockets-encoder", "pre-encoder", new DebugHandler( "PRE_ENCODE" ) );
                pipeline.addAfter( "websockets-decoder", "post-encoder", new DebugHandler( "POST_ENCODE" ) );
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
    private WebSocketChallenge challenge;
    private ChannelStateEvent connectedEvent;

}
