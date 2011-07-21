package org.projectodd.stilts.stomp.client.protocol.websockets;

import static org.junit.Assert.*;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.DownstreamChannelStateEvent;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.junit.Test;
import org.projectodd.stilts.stomp.client.protocol.StompDisconnectionNegotiator;
import org.projectodd.stilts.stomp.protocol.HandlerEmbedder;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class WebSocketDisconnectionNegotiatorTest {

    @Test
    public void testCloseNegotiation() {
        HandlerEmbedder handler = new HandlerEmbedder( false, new WebSocketDisconnectionNegotiator(), new StompDisconnectionNegotiator() ) ;
        
        Channel channel = handler.getChannel();
        
        assertNull( handler.poll() );
        channel.close();
        
        StompControlFrame stompCloseFrame = (StompControlFrame) handler.poll();
        assertNotNull( stompCloseFrame );
        assertEquals( Command.DISCONNECT, stompCloseFrame.getCommand() );
        assertNotNull( stompCloseFrame.getHeader( Header.RECEIPT ) );
        
        assertNull( handler.poll() );
        
        StompFrame receiptFrame = StompFrames.newReceiptFrame( stompCloseFrame.getHeader( Header.RECEIPT ) );
        handler.sendUpstream( receiptFrame );
        
        WebSocketFrame webSocketCloseFrame = (WebSocketFrame) handler.poll();
        assertNotNull( webSocketCloseFrame );
        
        assertNull( handler.poll() );
        handler.sendUpstream( webSocketCloseFrame );
        
        DownstreamChannelStateEvent finalCloseEvent = (DownstreamChannelStateEvent) handler.poll();
        assertNotNull( finalCloseEvent );
        assertEquals( ChannelState.OPEN, finalCloseEvent.getState() );
        assertFalse( Boolean.TRUE.equals( finalCloseEvent.getValue() ) );
    }

}
