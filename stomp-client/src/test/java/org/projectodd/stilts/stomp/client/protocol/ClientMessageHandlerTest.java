package org.projectodd.stilts.stomp.client.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;

public class ClientMessageHandlerTest extends AbstractClientProtocolHandlerTest<ClientMessageHandler> {

    @Override
    public ClientMessageHandler getHandler() {
        return new ClientMessageHandler( this.clientContext );
    }
    
    @Test
    public void testNormalMessage() {
        StompMessage message = StompMessages.createStompMessage( "/queues/foo", "everybody loves sandwiches" );
        this.handler.offer( message  );
        
        assertEquals( 0, this.clientContext.getErrors().size() );
        assertEquals( 1, this.clientContext.getMessages().size() );
        
        StompMessage receivedMessage = this.clientContext.getMessages().get(0);
        
        assertNotNull( receivedMessage );
        
        assertSame( message, receivedMessage );
        
        assertEquals( "/queues/foo", receivedMessage.getDestination() );
        assertEquals( "everybody loves sandwiches", receivedMessage.getContentAsString() );
    }
    
    @Test
    public void testErrorMessage() {
        StompMessage message = StompMessages.createStompErrorMessage( "everybody loves sandwiches" );
        this.handler.offer( message  );
        
        assertEquals( 1, this.clientContext.getErrors().size() );
        assertEquals( 0, this.clientContext.getMessages().size() );
        
        StompMessage receivedMessage = this.clientContext.getErrors().get(0);
        
        assertNotNull( receivedMessage );
        
        assertSame( message, receivedMessage );
        
        assertEquals( "everybody loves sandwiches", receivedMessage.getHeaders().get(  Header.MESSAGE ) );
    }

}
