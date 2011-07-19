package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.DownstreamMessageEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpRequestEncoder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class WebSocketConnectionNegotiator extends SimpleChannelUpstreamHandler {

    public WebSocketConnectionNegotiator() throws NoSuchAlgorithmException {
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

        request.setContent( ChannelBuffers.copiedBuffer( this.challenge.getKey3() ) );

        Channel channel = context.getChannel();

        context.sendDownstream( new DownstreamMessageEvent( channel, Channels.future( channel ), request, channel.getRemoteAddress() ) );
        super.channelConnected( context, e );
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) e.getMessage();

            ChannelBuffer content = response.getContent();

            byte[] challengeResponse = new byte[16];
            content.readBytes( challengeResponse );

            if (this.challenge.verify( challengeResponse )) {
                ChannelPipeline pipeline = context.getPipeline();
                pipeline.remove( HttpRequestEncoder.class );
                pipeline.remove( HttpResponseDecoder.class );
                pipeline.remove( this );
            }
        } else {
            super.messageReceived( context, e );
        }
    }

    private String host;
    private int port;
    private WebSocketChallenge challenge;

}
