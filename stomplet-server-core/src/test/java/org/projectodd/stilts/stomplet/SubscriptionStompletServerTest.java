package org.projectodd.stilts.stomplet;

import static org.junit.Assert.*;

import org.junit.Test;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomplet.container.SimpleStompletContainer;

public class SubscriptionStompletServerTest extends AbstractStompletServerTestCase {
    
    @Override
    public void configureServer() throws Exception {
        this.defaultContainer = new SimpleStompletContainer();
        getServer().setDefaultContainer( this.defaultContainer );
        
        this.queueOneStomplet = new MockAcknowledgeableStomplet();
        this.defaultContainer.addStomplet( "/queues/one", this.queueOneStomplet );
    }
    
    @Test
    public void testSubscription() throws Exception {
        StompClient client = new StompClient( "localhost" );
        client.connect();
        
        ClientSubscription subscription = client.subscribe( "/queues/one" ).start();
        assertNotNull( subscription );
        
        assertEquals( 1, this.queueOneStomplet.getSubscribers().size() );
        assertEquals( 0, this.queueOneStomplet.getUnsubscribers().size() );
        
        subscription.unsubscribe();
        
        assertEquals( 1, this.queueOneStomplet.getSubscribers().size() );
        assertEquals( 1, this.queueOneStomplet.getUnsubscribers().size() );
        
        client.disconnect();
        
        assertEquals( 1, this.queueOneStomplet.getSubscribers().size() );
        assertEquals( 1, this.queueOneStomplet.getUnsubscribers().size() );
    }
    
    @Test
    public void testSubscriptionImplicitUnsubscribe() throws Exception {
        StompClient client = new StompClient( "localhost" );
        client.connect();
        
        ClientSubscription subscription = client.subscribe( "/queues/one" ).start();
        assertNotNull( subscription );
        
        assertEquals( 1, this.queueOneStomplet.getSubscribers().size() );
        assertEquals( 0, this.queueOneStomplet.getUnsubscribers().size() );
        
        client.disconnect();
        
        assertEquals( 1, this.queueOneStomplet.getSubscribers().size() );
        assertEquals( 1, this.queueOneStomplet.getUnsubscribers().size() );
    }
    

    private SimpleStompletContainer defaultContainer;
    private MockStomplet queueOneStomplet;


}
