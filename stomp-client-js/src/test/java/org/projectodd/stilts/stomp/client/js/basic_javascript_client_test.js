
jspec.load( "/stilts-stomp.js" );

it( "should be able to send a message", function() {
  client = Stomp.client( "ws://localhost:8675/" );

  client.connect( null, null, function(frame) {
    Assert.assertEquals( "1.1", client.version );
    
    client.send("/queues/one", {priority: 9}, "content 1");
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.sends.size(), 0 );
  
    send = connection.sends.get( 0 );
    message = send.message;
    Assert.assertEquals( "/queues/one", message.destination );
    Assert.assertEquals( "content 1", message.contentAsString );
    
    client.disconnect();
  } );
  
  client.waitForDisconnect();
  
} );

it( "should be able to manage subscriptions", function() {
  client = Stomp.client( "ws://localhost:8675/" );

  client.connect( null, null, function(frame) {
    connected = true;
    Assert.assertEquals( "1.1", client.version );
    
    subscription_id = client.subscribe("/queues/one", function(msg) {
    } );
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.subscriptions.size(), 0 );
    
    subscription = connection.subscriptions.get( subscription_id );
    Assert.assertEquals( "/queues/one", subscription.destination );
    
    client.unsubscribe( subscription_id );
    pause();
    
    Assert.assertEquals( 0, connection.subscriptions.size(), 0 );
  
    client.disconnect();
  } );
  
  client.waitForDisconnect();
} );