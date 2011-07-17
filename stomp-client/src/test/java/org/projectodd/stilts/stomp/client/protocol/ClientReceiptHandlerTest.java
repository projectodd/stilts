package org.projectodd.stilts.stomp.client.protocol;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrames;

public class ClientReceiptHandlerTest extends AbstractClientProtocolHandlerTest<ClientReceiptHandler> {

    @Override
    public ClientReceiptHandler getHandler() {
        return new ClientReceiptHandler( this.clientContext );
    }
    
    @Test
    public void testReceipt() {
        StompFrame frame = StompFrames.newReceiptFrame( "message-22" );
        this.handler.offer( frame  );
        
        assertEquals( 1, this.clientContext.getReceipts().size() );
        assertEquals( "message-22", this.clientContext.getReceipts().get( 0 ));
    }

}
