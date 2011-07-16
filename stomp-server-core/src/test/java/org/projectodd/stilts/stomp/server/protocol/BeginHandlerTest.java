package org.projectodd.stilts.stomp.server.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompContentFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;

public class BeginHandlerTest extends AbstractProtocolHandlerTest<BeginHandler> {
    
    @Test
    public void testErrorNoTxId() {
        StompFrame stompFrame = new StompFrame( Command.BEGIN );
        handler.offer( stompFrame );
        StompContentFrame frame = (StompContentFrame) handler.poll();
        assertEquals( Command.ERROR, frame.getCommand() );
        assertEquals( "Unable to begin transaction: No transaction ID supplied.", new String( frame.getContent().array() ) );
    }

    @Override
    public BeginHandler getHandler() {
        return new BeginHandler(server.getStompProvider(), ctx);
    }

}
