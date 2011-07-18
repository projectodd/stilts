package org.projectodd.stilts.conduit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.client.ClientTransaction;
import org.projectodd.stilts.stomp.client.StompClient;

public class BasicConduitServerTest extends AbstractConduitServerTestCase<MockMessageConduitFactory> {

    @Override
    protected ConduitServer<MockMessageConduitFactory> createServer() throws Exception {
        ConduitServer<MockMessageConduitFactory> server = new ConduitServer<MockMessageConduitFactory>();
        server.setMessageConduitFactory( new MockMessageConduitFactory() );
        return server;
    }
    
    @Test
    public void testServerCreation() {
        assertNotNull( this.server );
        assertNotNull( this.server.getMessageConduitFactory() );
        assertNotNull( this.server.getTransactionManager() );
    }
    
    @Test
    public void testClientConnection() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        assertTrue( client.isConnected() );
        client.disconnect();
        assertTrue( client.isDisconnected() );
    }
    
    @Test
    public void testClientSendWithoutTransaction() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        
        client.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        client.disconnect();
        
        MockMessageConduit conduit = getServer().getMessageConduitFactory().getConduits().get( 0 );
        assertNotNull( conduit );
        
        assertEquals( 1, conduit.getMessages().size() );
        
        StompMessage message = conduit.getMessages().get( 0 );
        
        assertNotNull( message );
        
        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
    }
    
    @Test
    public void testClientSendWithTransactionCommit() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        
        ClientTransaction tx = client.begin();
        
        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        
        tx.commit();
        client.disconnect();
        
        MockMessageConduit conduit = getServer().getMessageConduitFactory().getConduits().get( 0 );
        assertNotNull( conduit );
        
        assertEquals( 1, conduit.getMessages().size() );
        
        StompMessage message = conduit.getMessages().get( 0 );
        
        assertNotNull( message );
        
        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
    }
    
    @Test
    public void testClientSendWithTransactionAbort() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        
        ClientTransaction tx = client.begin();
        
        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        tx.abort();
        client.disconnect();
        
        MockMessageConduit conduit = getServer().getMessageConduitFactory().getConduits().get( 0 );
        assertNotNull( conduit );
        
        assertEquals( 0, conduit.getMessages().size() );
    }

}
