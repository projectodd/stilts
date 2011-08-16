package org.projectodd.stilts.stomplet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.projectodd.stilts.conduit.stomp.SimpleStompSessionManager;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.client.ClientTransaction;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;

public class BasicStompletServerTest extends AbstractStompletServerTestCase {
    
    @Override
    public void configureServer() throws Exception {
        this.defaultContainer = new SimpleStompletContainer();
        getServer().setDefaultContainer( this.defaultContainer );
        
        this.queueOneStomplet = new MockAcknowledgeableStomplet();
        this.defaultContainer.addStomplet( "/queues/one", this.queueOneStomplet );
        
        this.defaultSessionManager = new SimpleStompSessionManager();
        getServer().setDefaultSessionManager( this.defaultSessionManager );
    }
    
    public String getConnectionUrl() {
        return "stomp://localhost/";
    }


    @Test
    public void testServerCreation() {
        assertNotNull( this.server );
        assertNotNull( this.server.getTransactionManager() );
    }
    
    @Test
    public void testClientConnection() throws Exception {
        StompClient client = new StompClient( getConnectionUrl() );
        client.connect();
        assertTrue( client.isConnected() );
        client.disconnect();
        assertTrue( client.isDisconnected() );
    }
    
    @Test
    public void testClientSendWithoutTransaction() throws Exception {
        StompClient client = new StompClient( getConnectionUrl() );
        client.connect();
        
        client.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        client.disconnect();
        
        assertEquals( 1, this.queueOneStomplet.getMessages().size() );
        
        StompMessage message = this.queueOneStomplet.getMessages().get( 0 );
        
        assertNotNull( message );
        
        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
    }
    
    @Test
    public void testClientSendWithTransactionCommit() throws Exception {
        StompClient client = new StompClient( getConnectionUrl() );
        client.connect();
        
        ClientTransaction tx = client.begin();
        
        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        
        tx.commit();
        client.disconnect();
        
        assertEquals( 1, this.queueOneStomplet.getMessages().size() );
        
        StompMessage message = this.queueOneStomplet.getMessages().get( 0 );
        
        assertNotNull( message );
        
        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
        
    }
    
    @Test
    public void testClientSendWithTransactionAbort() throws Exception {
        StompClient client = new StompClient( getConnectionUrl() );
        client.connect();
        
        ClientTransaction tx = client.begin();
        
        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        tx.abort();
        client.disconnect();
        
        assertEquals( 0, this.queueOneStomplet.getMessages().size() );
    }

    private SimpleStompletContainer defaultContainer;
    private SimpleStompSessionManager defaultSessionManager;
    private MockStomplet queueOneStomplet;


}
