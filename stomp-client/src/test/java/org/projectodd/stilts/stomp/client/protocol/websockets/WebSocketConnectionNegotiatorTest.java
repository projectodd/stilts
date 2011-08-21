package org.projectodd.stilts.stomp.client.protocol.websockets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.UpstreamChannelStateEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.HandlerEmbedder;
import org.projectodd.stilts.stomp.protocol.websocket.ietf00.Ietf00Handshake;
import org.projectodd.stilts.stomp.protocol.websocket.ietf00.Ietf00WebSocketChallenge;

public class WebSocketConnectionNegotiatorTest {

    @Test
    public void testConnect() throws Exception {
        HandlerEmbedder handler = new HandlerEmbedder( false, new WebSocketHttpResponseDecoder(), new WebSocketConnectionNegotiator( "localhost", 8675, new Ietf00Handshake()  ) );
        
        ChannelPipeline pipeline = handler.getPipeline();
        
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
        
        handler.sendUpstream( new UpstreamChannelStateEvent( handler.getChannel(), ChannelState.CONNECTED, handler.getChannel().getRemoteAddress() ) );

        HttpRequest result = (HttpRequest) handler.poll();
        assertNotNull( result );

        String key1 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1 );
        assertNotNull( key1 );
        
        String key2 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2 );
        assertNotNull( key2 );
        
        assertEquals( 10, result.getContent().readableBytes() );
        
        byte[] key3 = new byte[8];
        result.getContent().readBytes( key3 );
        
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
        
        byte[] solution = Ietf00WebSocketChallenge.solve( key1, key2, key3 );
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(16);
        buffer.writeBytes( solution );
        httpResponse.setContent( buffer );
        
        handler.sendUpstream( httpResponse); 
        
        // reconfigured
        assertNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
    }
    
    @Test
    public void testConnectFailure() throws Exception {
        HandlerEmbedder handler = new HandlerEmbedder( false, new WebSocketHttpResponseDecoder(), new WebSocketConnectionNegotiator( "localhost", 8675, new Ietf00Handshake() ) );
        
        ChannelPipeline pipeline = handler.getPipeline();
        
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
        
        handler.sendUpstream( new UpstreamChannelStateEvent( handler.getChannel(), ChannelState.CONNECTED, handler.getChannel().getRemoteAddress() ) );

        HttpRequest result = (HttpRequest) handler.poll();
        assertNotNull( result );

        String key1 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1 );
        assertNotNull( key1 );
        
        String key2 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2 );
        assertNotNull( key2 );
        
        assertEquals( 10, result.getContent().readableBytes() );
        
        byte[] key3 = new byte[8];
        result.getContent().readBytes( key3 );
        
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
        
        byte[] solution = Ietf00WebSocketChallenge.solve( key1, key2, key3 );
        
        // break the solution
        solution[2] += 2;
        
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(16);
        buffer.writeBytes( solution );
        httpResponse.setContent( buffer );
        
        handler.sendUpstream( httpResponse); 
        
        // did not reconfigure
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
    }

}
