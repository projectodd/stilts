package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.projectodd.stilts.stomp.MockSubscription;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.server.MockStompConnection;

public class SubscribeHandlerTest extends AbstractServerProtocolFrameHandlerTest<SubscribeHandler> {

    @Override
    public SubscribeHandler getHandler() {
        return new SubscribeHandler( server.getStompProvider(), ctx );
    }

    @Test
    public void testErrorNoDestination() {
        StompFrame stompFrame = new StompFrame( Command.SUBSCRIBE );
        stompFrame.setHeader( Header.ID, "123" );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Cannot subscribe without destination.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testErrorNoId() {
        StompFrame stompFrame = new StompFrame( Command.SUBSCRIBE );
        stompFrame.setHeader( Header.DESTINATION, "/queues/foobaria" );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Cannot subscribe without ID.", new String( frame.getContent().array() ) );
    }

    @Test
    public void testValidSubscription() {
        StompFrame stompFrame = new StompFrame( Command.SUBSCRIBE );
        stompFrame.setHeader( Header.DESTINATION, "/queues/foobaria" );
        stompFrame.setHeader( Header.ID, "0" );
        handler.offer( stompFrame );
        StompFrame frame = handler.poll();
        assertEquals( Command.SUBSCRIBE, frame.getCommand() );
        MockStompConnection connection = server.getStompProvider().getConnections().get( 0 );
        MockSubscription sub = (MockSubscription) connection.getSubscriptions().get( "0" );
        assertEquals( "0", sub.getId() );
        assertEquals( "/queues/foobaria", sub.getDestination() );
    }

}
