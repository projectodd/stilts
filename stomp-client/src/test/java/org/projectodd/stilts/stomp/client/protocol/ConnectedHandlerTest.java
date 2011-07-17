package org.projectodd.stilts.stomp.client.protocol;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.stomp.client.StompClient.State;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.protocol.StompFrames;


public class ConnectedHandlerTest extends AbstractClientProtocolHandlerTest<ConnectedHandler> {

    @Override
    public ConnectedHandler getHandler() {
        return new ConnectedHandler( this.clientContext );
    }
    
    @Test
    public void testGoodConnection() {
        StompFrame frame = StompFrames.newConnectedFrame( "session-foo", Version.VERSION_1_1 );
        this.clientContext.setConnectionState( null );
        this.handler.offer( frame );
        assertSame( State.CONNECTED, this.clientContext.getConnectionState() );
    }
    
    @Test
    public void testErrorConnection() {
        StompFrame frame = StompFrames.newErrorFrame( "It broke", null );
        this.clientContext.setConnectionState( null );
        this.handler.offer( frame );
        assertNull( this.clientContext.getConnectionState() );
    }
}
