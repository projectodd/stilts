package org.projectodd.stilts.stomplet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;
import org.projectodd.stilts.conduit.stomp.SimpleStompSessionManager;
import org.projectodd.stilts.stomp.StompMessage;
import org.projectodd.stilts.stomp.StompMessages;
import org.projectodd.stilts.stomp.Subscription.AckMode;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.helpers.MessageAccumulator;
import org.projectodd.stilts.stomplet.MockAcknowledgeableStomplet.Ack;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;

public class AckStompletServerTest extends AbstractStompletServerTestCase {

    @Override
    public void configureServer() throws Exception {
        this.defaultContainer = new SimpleStompletContainer();
        this.defaultSessionManager = new SimpleStompSessionManager();
        getServer().setDefaultContainer( this.defaultContainer );
        getServer().setDefaultSessionManager( this.defaultSessionManager );

        this.queueOneStomplet = new MockAcknowledgeableStomplet();
        this.defaultContainer.addStomplet( "/queues/one", this.queueOneStomplet );
    }

    @Test
    public void testAutoAck() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        MessageAccumulator accumulator = new MessageAccumulator();
        ClientSubscription subscription = client.subscribe( "/queues/one" ).withMessageHandler( accumulator ).start();
        this.queueOneStomplet.send( StompMessages.createStompMessage( "/queues/one", "sent from stomplet" ));
        
        Thread.sleep(  500 );
        
        assertEquals( 1, accumulator.getMessages().size() );
        StompMessage message = accumulator.getMessages().get( 0 );
        assertNotNull( message );
        assertEquals( "sent from stomplet", message.getContentAsString() );
        
        assertEquals( 1, this.queueOneStomplet.getAcks().size() );
        subscription.unsubscribe();
        client.disconnect();
    }
    
    @Test
    public void testClientIndividualAck() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        
        MessageAccumulator accumulator = new MessageAccumulator();
        
        ClientSubscription subscription = client.subscribe( "/queues/one" )
                                                .withMessageHandler( accumulator )
                                                .withAckMode( AckMode.CLIENT_INDIVIDUAL )
                                                .start();
        
        StompMessage sentMessage1 = StompMessages.createStompMessage( "/queues/one", "sent from stomplet #1" );
        StompMessage sentMessage2 = StompMessages.createStompMessage( "/queues/one", "sent from stomplet #2" );
        
        this.queueOneStomplet.send( sentMessage1 );
        this.queueOneStomplet.send( sentMessage2 );
        
        Thread.sleep(  500 );
        
        assertEquals( 2, accumulator.getMessages().size() );
        
        StompMessage message1 = accumulator.getMessages().get( 0 );
        assertNotNull( message1 );
        assertEquals( "sent from stomplet #1", message1.getContentAsString() );
        
        StompMessage message2 = accumulator.getMessages().get( 1 );
        assertNotNull( message2 );
        assertEquals( "sent from stomplet #2", message2.getContentAsString() );
        
        assertEquals( 0, this.queueOneStomplet.getAcks().size() );
        assertEquals( 0, this.queueOneStomplet.getNacks().size() );
        
        message1.ack();
        message2.nack();
        
        Thread.sleep(  500 );
        
        assertEquals( 1, this.queueOneStomplet.getAcks().size() );
        assertEquals( 1, this.queueOneStomplet.getNacks().size() );
        
        assertContains( this.queueOneStomplet.getAcks(), message1.getId() );
        assertContains( this.queueOneStomplet.getNacks(), message2.getId() );
        
        subscription.unsubscribe();
        client.disconnect();
    }
    
    @Test
    public void testClientIndividualAckImplicitNack() throws Exception {
        StompClient client = new StompClient( "stomp://localhost/" );
        client.connect();
        
        MessageAccumulator accumulator = new MessageAccumulator();
        
        ClientSubscription subscription = client.subscribe( "/queues/one" )
                                                .withMessageHandler( accumulator )
                                                .withAckMode( AckMode.CLIENT_INDIVIDUAL )
                                                .start();
        
        StompMessage sentMessage1 = StompMessages.createStompMessage( "/queues/one", "sent from stomplet #1" );
        StompMessage sentMessage2 = StompMessages.createStompMessage( "/queues/one", "sent from stomplet #2" );
        
        this.queueOneStomplet.send( sentMessage1 );
        this.queueOneStomplet.send( sentMessage2 );
        
        Thread.sleep(  500 );
        
        assertEquals( 2, accumulator.getMessages().size() );
        
        StompMessage message1 = accumulator.getMessages().get( 0 );
        assertNotNull( message1 );
        assertEquals( "sent from stomplet #1", message1.getContentAsString() );
        
        StompMessage message2 = accumulator.getMessages().get( 1 );
        assertNotNull( message2 );
        assertEquals( "sent from stomplet #2", message2.getContentAsString() );
        
        assertEquals( 0, this.queueOneStomplet.getAcks().size() );
        assertEquals( 0, this.queueOneStomplet.getNacks().size() );
        
        subscription.unsubscribe();
        client.disconnect();
        
        assertEquals( 0, this.queueOneStomplet.getAcks().size() );
        assertEquals( 2, this.queueOneStomplet.getNacks().size() );
        
        assertContains( this.queueOneStomplet.getNacks(), message1.getId() );
        assertContains( this.queueOneStomplet.getNacks(), message2.getId() );
    }
    
    protected void assertContains(Collection<Ack> acks, String messageId) {
        for ( Ack each : acks ) {
            if ( each.message.getId().equals( messageId) ) {
                return;
            }
        }
        
        fail( "Expected "+ acks + " to include message-id: " + messageId );
        
    }

    private SimpleStompletContainer defaultContainer;
    private SimpleStompSessionManager defaultSessionManager;
    private MockAcknowledgeableStomplet queueOneStomplet;

}
