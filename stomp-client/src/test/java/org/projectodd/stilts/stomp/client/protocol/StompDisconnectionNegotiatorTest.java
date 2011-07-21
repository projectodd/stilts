package org.projectodd.stilts.stomp.client.protocol;

import static org.junit.Assert.*;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelState;
import org.jboss.netty.channel.DownstreamChannelStateEvent;
import org.junit.Test;
import org.projectodd.stilts.stomp.client.MockClientContext;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.protocol.HandlerEmbedder;
import org.projectodd.stilts.stomp.protocol.StompControlFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public class StompDisconnectionNegotiatorTest {

    @Test
    public void testCloseNegotiation() {
        MockClientContext clientContext = new MockClientContext();
        HandlerEmbedder handler = new HandlerEmbedder( false, new StompDisconnectionNegotiator( clientContext ) ) ;
        
        Channel channel = handler.getChannel();
        
        assertNull( handler.poll() );
        channel.close();
        
        StompControlFrame closeFrame = (StompControlFrame) handler.poll();
        assertNotNull( closeFrame );
        assertEquals( Command.DISCONNECT, closeFrame.getCommand() );
        assertNotNull( closeFrame.getHeader( Header.RECEIPT ) );
        
        assertNull( handler.poll() );
        
        StompFrame receiptFrame = StompFrames.newReceiptFrame( closeFrame.getHeader( Header.RECEIPT ) );
        handler.sendUpstream( receiptFrame );
        
        DownstreamChannelStateEvent finalCloseEvent = (DownstreamChannelStateEvent) handler.poll();
        assertNotNull( finalCloseEvent );
        assertEquals( ChannelState.OPEN, finalCloseEvent.getState() );
        assertFalse( Boolean.TRUE.equals( finalCloseEvent.getValue() ) );
        
        assertEquals( State.DISCONNECTED, clientContext.getConnectionState() );
    }

}
