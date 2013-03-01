package org.projectodd.stilts.stomp.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.client.ClientTransaction;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.server.MockStompConnection.Send;

public class BasicStompServerTest extends AbstractStompServerTestCase<MockStompProvider> {

    @Override
    protected StompServer<MockStompProvider> createServer() throws Exception {
        StompServer<MockStompProvider> server = new StompServer<MockStompProvider>();
        server.setStompProvider( new MockStompProvider() );
        server.addConnector( new InsecureConnector() );
        return server;
    }
    
    @Before
    public void setUpClient() throws Exception {
        this.client = createClient();
    }
    
    public StompClient createClient() throws Exception {
        return new StompClient( getConnectionUrl() );
    }
    
    public String getConnectionUrl() {
        return "stomp://localhost/";
    }

    @Test
    public void testServerCreation() {
        assertNotNull( this.server );
        assertNotNull( this.server.getStompProvider() );
    }

    @Test
    public void testClientConnection() throws Exception {
        client.connect();
        assertTrue( client.isConnected() );
        client.disconnect();
        assertTrue( client.isDisconnected() );
    }
    
    @Test
    public void testClientSendWithoutTransaction() throws Exception {
        client.connect();

        client.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );
        client.disconnect();

        MockStompConnection connection = getServer().getStompProvider().getConnections().get( 0 );
        assertNotNull( connection );

        assertEquals( 1, connection.getSends().size() );

        Send send = connection.getSends().get( 0 );
        assertNull( send.transactionId );

        StompMessage message = send.message;
        assertNotNull( message );

        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
    }

    @Test
    public void testClientSendWithTransactionCommit() throws Exception {
        client.connect();

        ClientTransaction tx = client.begin();

        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );

        tx.commit();
        client.disconnect();

        MockStompConnection connection = getServer().getStompProvider().getConnections().get( 0 );
        assertNotNull( connection );

        assertEquals( 1, connection.getSends().size() );

        Send send = connection.getSends().get( 0 );
        assertNotNull( send.transactionId );

        StompMessage message = send.message;
        assertNotNull( message );

        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );
        
        assertEquals( 1, connection.getCommits().size() );
        assertEquals( 0, connection.getAborts().size() );
        
        assertEquals( send.transactionId, connection.getBegins().get(0) );
        assertEquals( send.transactionId, connection.getCommits().get(0) );
    }
    
    @Test
    public void testClientSendWithTransactionAbort() throws Exception {
        client.connect();

        ClientTransaction tx = client.begin();

        tx.send( StompMessages.createStompMessage( "/queues/one", "content 1" ) );

        tx.abort();
        client.disconnect();
        
        MockStompConnection connection = getServer().getStompProvider().getConnections().get( 0 );
        assertNotNull( connection );

        assertEquals( 1, connection.getSends().size() );

        Send send = connection.getSends().get( 0 );
        assertNotNull( send.transactionId );

        StompMessage message = send.message;
        assertNotNull( message );

        assertEquals( "/queues/one", message.getDestination() );
        assertEquals( "content 1", message.getContentAsString() );

        assertEquals( 0, connection.getCommits().size() );
        assertEquals( 1, connection.getAborts().size() );
        
        assertEquals( send.transactionId, connection.getBegins().get(0) );
        assertEquals( send.transactionId, connection.getAborts().get(0) );
    }

    private StompClient client;
}
