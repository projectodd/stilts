package org.projectodd.stilts.stomp.client.protocol.websockets;

import static org.junit.Assert.*;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.PipelineExposer;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketChallenge;

public class WebSocketConnectionNegotiatorTest {

    @Test
    public void testConnect() throws Exception {
        PipelineExposer pipelineExposer = new PipelineExposer();
        DecoderEmbedder<HttpRequest> handler = new DecoderEmbedder<HttpRequest>( pipelineExposer, new WebSocketHttpResponseDecoder(), new WebSocketConnectionNegotiator( "localhost", 8675 ) );
        
        ChannelPipeline pipeline = pipelineExposer.getPipeline();
        
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );

        HttpRequest result = handler.poll();
        assertNotNull( result );

        String key1 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1 );
        assertNotNull( key1 );
        
        String key2 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2 );
        assertNotNull( key2 );
        
        assertEquals( 10, result.getContent().readableBytes() );
        
        byte[] key3 = new byte[8];
        result.getContent().readBytes( key3 );
        
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
        
        byte[] solution = WebSocketChallenge.solve( key1, key2, key3 );
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(16);
        buffer.writeBytes( solution );
        httpResponse.setContent( buffer );
        
        handler.offer( httpResponse); 
        
        // reconfigured
        assertNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
    }
    
    @Test
    public void testConnectFailure() throws Exception {
        PipelineExposer pipelineExposer = new PipelineExposer();
        DecoderEmbedder<HttpRequest> handler = new DecoderEmbedder<HttpRequest>( pipelineExposer, new WebSocketHttpResponseDecoder(), new WebSocketConnectionNegotiator( "localhost", 8675 ) );
        
        ChannelPipeline pipeline = pipelineExposer.getPipeline();
        
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );

        HttpRequest result = handler.poll();
        assertNotNull( result );

        String key1 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY1 );
        assertNotNull( key1 );
        
        String key2 = result.getHeader( HttpHeaders.Names.SEC_WEBSOCKET_KEY2 );
        assertNotNull( key2 );
        
        assertEquals( 10, result.getContent().readableBytes() );
        
        byte[] key3 = new byte[8];
        result.getContent().readBytes( key3 );
        
        DefaultHttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK );
        
        byte[] solution = WebSocketChallenge.solve( key1, key2, key3 );
        
        // break the solution
        solution[2] += 2;
        
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer(16);
        buffer.writeBytes( solution );
        httpResponse.setContent( buffer );
        
        handler.offer( httpResponse); 
        
        // did not reconfigure
        assertNotNull( pipeline.get( WebSocketHttpResponseDecoder.class ) );
    }

}
