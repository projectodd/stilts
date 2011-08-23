package org.projectodd.stilts.stomp.client.protocol.websockets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.DownstreamChannelStateEvent;
import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.HandlerEmbedder;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketDisconnectionNegotiator;
import org.projectodd.stilts.stomp.protocol.websocket.WebSocketFrame;

public class StompWebSocketDisconnectionNegotiationTest {

    @Test
    public void testCloseNegotiation() {
        HandlerEmbedder handler = new HandlerEmbedder( false, new WebSocketDisconnectionNegotiator() ) ;
        
        Channel channel = handler.getChannel();
        
        assertNull( handler.poll() );
        channel.close();
        
        WebSocketFrame closeFrame = (WebSocketFrame) handler.poll();
        assertNotNull( closeFrame );
        
        assertNull( handler.poll() );
        handler.sendUpstream( closeFrame );
        
        DownstreamChannelStateEvent finalCloseEvent = (DownstreamChannelStateEvent) handler.poll();
        assertNotNull( finalCloseEvent );
        assertEquals( ChannelState.OPEN, finalCloseEvent.getState() );
        assertFalse( Boolean.TRUE.equals( finalCloseEvent.getValue() ) );
    }

}
