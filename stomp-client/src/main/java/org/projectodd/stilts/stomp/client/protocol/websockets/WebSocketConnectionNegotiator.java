package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.security.NoSuchAlgorithmException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.UpstreamChannelStateEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;

public class WebSocketConnectionNegotiator extends SimpleChannelUpstreamHandler {

    public WebSocketConnectionNegotiator(String host, int port) throws NoSuchAlgorithmException {
        this.host = host;
        this.port = port;
        this.challenge = new WebSocketChallenge();
    }

    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent e) throws Exception {
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

        System.err.println( "CLIENT SEND WS CHALLENGE: " + request );

        context.sendDownstream( new DownstreamMessageEvent( channel, Channels.future( channel ), request, channel.getRemoteAddress() ) );
        // super.channelConnected( context, e );
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent e) throws Exception {
        System.err.println( "CLIENT messageReceived: " + e );
        if (e.getMessage() instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) e.getMessage();

            ChannelBuffer content = response.getContent();

            byte[] challengeResponse = new byte[16];
            System.err.println( "CONTENT: " + HttpHeaders.getContentLength( response ) );
            content.readBytes( challengeResponse );

            System.err.println( "CLIENT verifying: " + challengeResponse.length);
            if (this.challenge.verify( challengeResponse )) {
                ChannelPipeline pipeline = context.getPipeline();
                pipeline.remove( this );
                pipeline.replace( "http-decoder", "websockets-decoder", new WebSocketFrameDecoder() );
                pipeline.replace( "http-encoder", "websockets-encoder", new WebSocketFrameEncoder() );
                Channel channel = context.getChannel();
                context.sendUpstream( new UpstreamChannelStateEvent( channel, ChannelState.CONNECTED, channel.getRemoteAddress() ) );
            }
        } else {
            super.messageReceived( context, e );
        }
    }

    private String host;
    private int port;
    private WebSocketChallenge challenge;

}
