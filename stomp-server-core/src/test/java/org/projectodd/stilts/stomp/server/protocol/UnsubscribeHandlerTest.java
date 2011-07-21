package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompConnection;

public class UnsubscribeHandlerTest extends AbstractServerProtocolFrameHandlerTest<UnsubscribeHandler> {

    @Override
    public UnsubscribeHandler getHandler() {
        return new UnsubscribeHandler( server.getStompProvider(), ctx );
    }

    @Test
    public void testErrorNoId() {
        StompFrame stompFrame = new StompFrame( Command.UNSUBSCRIBE );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Must supply 'id' header for UNSUBSCRIBE", new String( frame.getContent().array() ) );
    }

    @Test
    public void testNonExistentId() {
        StompFrame stompFrame = new StompFrame( Command.UNSUBSCRIBE );
        stompFrame.setHeader( Header.ID, "420abc" );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Invalid subscription id: 420abc", new String( frame.getContent().array() ) );
    }

    @Test
    public void testValidUnsubscribe() throws Exception {
        StompConnection connection = server.getStompProvider().getConnections().get( 0 );
        connection.subscribe( "/queue/foobaria", "1", null );
        StompFrame stompFrame = new StompFrame( Command.UNSUBSCRIBE );
        stompFrame.setHeader( Header.ID, "1" );
        handler.offer( stompFrame );
        StompFrame frame = handler.poll();
        assertEquals( Command.UNSUBSCRIBE, frame.getCommand() );
        assertEquals( "1", frame.getHeader( Header.ID ) );
    }

}
