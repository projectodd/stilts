
jspec.load( "/stilts-stomp.js" );

it( "should be able to send a message", function() {
  log( Assert );
  
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

it( "should be able to commit a transaction", function() {
  client = Stomp.client( "ws://localhost:8675/" );

  client.connect( null, null, function(frame) {
    Assert.assertEquals( "1.1", client.version );
    
    client.begin( "tx-1" );
    client.send("/queues/one", {transaction: "tx-1"}, "message in a transaction");
    
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.sends.size(), 0 );
  
    send = connection.sends.get( 0 );
    message = send.message;
    Assert.assertEquals( "/queues/one", message.destination );
    Assert.assertEquals( "message in a transaction", message.contentAsString );
    Assert.assertEquals( "tx-1", message.headers.get( "transaction" ) );
    
    Assert.assertEquals( 1, connection.begins.size(), 0 );
    Assert.assertEquals( "tx-1", connection.begins.get( 0 ) );
    
    Assert.assertEquals( 0, connection.commits.size(), 0 );
    
    client.commit( "tx-1" );
    pause();
    
    Assert.assertEquals( 1, connection.begins.size(), 0 );
    Assert.assertEquals( "tx-1", connection.begins.get( 0 ) );
    
    Assert.assertEquals( 1, connection.commits.size(), 0 );
    Assert.assertEquals( "tx-1", connection.commits.get( 0 ) );
    
    client.disconnect();
  } );
  
  client.waitForDisconnect();

});

it( "should be able to abort a transactions", function() {
  client = Stomp.client( "ws://localhost:8675/" );

  client.connect( null, null, function(frame) {
    Assert.assertEquals( "1.1", client.version );
    
    client.begin( "tx-1" );
    client.send("/queues/one", {transaction: "tx-1"}, "message in a transaction");
    
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.sends.size(), 0 );
  
    send = connection.sends.get( 0 );
    message = send.message;
    Assert.assertEquals( "/queues/one", message.destination );
    Assert.assertEquals( "message in a transaction", message.contentAsString );
    Assert.assertEquals( "tx-1", message.headers.get( "transaction" ) );
    
    Assert.assertEquals( 1, connection.begins.size(), 0 );
    Assert.assertEquals( "tx-1", connection.begins.get( 0 ) );
    
    Assert.assertEquals( 0, connection.commits.size(), 0 );
    
    client.abort( "tx-1" );
    pause();
    
    Assert.assertEquals( 1, connection.begins.size(), 0 );
    Assert.assertEquals( "tx-1", connection.begins.get( 0 ) );
    
    Assert.assertEquals( 1, connection.aborts.size(), 0 );
    Assert.assertEquals( "tx-1", connection.aborts.get( 0 ) );
    
    client.disconnect();
  } );
  
  client.waitForDisconnect();

});






