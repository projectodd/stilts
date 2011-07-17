package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.projectodd.stilts.stomp.DefaultHeaders;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public class AckHandlerTest extends AbstractServerProtocolFrameHandlerTest<AckHandler> {

    @Test
    public void testAckWithoutTransaction() {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        ctx.getAckManager().registerAcknowledger( "message-98", acknowledger );
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        frame.setHeader( Header.SUBSCRIPTION, "subscription-44" );
        frame.setHeader( Header.MESSAGE_ID, "message-98" );

        this.handler.offer( frame );

        assertEquals( 1, acknowledger.getAcks().size() );
        assertEquals( 0, acknowledger.getNacks().size() );

        assertNull( acknowledger.getAcks().get( 0 ) );
    }

    @Test
    public void testAckWithTransaction() {
        MockTransactionalAcknowledger acknowledger = new MockTransactionalAcknowledger();
        ctx.getAckManager().registerAcknowledger( "message-98", acknowledger );
        StompFrame frame = StompFrames.newAckFrame( new DefaultHeaders() );
        frame.setHeader( Header.SUBSCRIPTION, "subscription-44" );
        frame.setHeader( Header.MESSAGE_ID, "message-98" );
        frame.setHeader( Header.TRANSACTION, "transaction-bob" );

        this.handler.offer( frame );

        assertEquals( 1, acknowledger.getAcks().size() );
        assertEquals( 0, acknowledger.getNacks().size() );

        assertEquals( "transaction-bob", acknowledger.getAcks().get( 0 ) );
    }

    @Override
    public AckHandler getHandler() {
        return new AckHandler( server.getStompProvider(), ctx );
    }

}
