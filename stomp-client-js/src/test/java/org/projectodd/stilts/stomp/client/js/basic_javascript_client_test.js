
jspec.load( "/stomp.js" );


it( "should be able to send a message", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
    client.send("/queues/one", {priority: 9}, "ääjj");
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.sends.size(), 0 );
  
    send = connection.sends.get( 0 );
    message = send.message;
    Assert.assertEquals( "/queues/one", message.destination );
    Assert.assertEquals( "ääjj", message.contentAsString );
    
    client.disconnect( function() { latch.countDown(); } );
  } );

  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );
  
} );

it( "should be able to send a UTF8 message", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
    client.send("/queues/one", {priority: 9}, "content 1");
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.sends.size(), 0 );
  
    send = connection.sends.get( 0 );
    message = send.message;
    Assert.assertEquals( "/queues/one", message.destination );
    Assert.assertEquals( "content 1", message.contentAsString );
    
    client.disconnect( function() { latch.countDown(); } );
  } );

  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );
  
} );

it( "should be able to manage subscriptions", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
    
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
  
    client.disconnect( function() { latch.countDown(); } );
  } );
  
  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );
} );

it( "should be able to commit a transaction", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
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
    
    client.disconnect( function() { latch.countDown(); } );
  } );
  
  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );

});

it( "should be able to abort a transactions", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
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
    
    client.disconnect( function() { latch.countDown(); } );
  } );
  
  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );

});

it( "should be able to receive messages", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;
  
  var receivedMessage = null;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
    subscription_id = client.subscribe("/queues/one", function(msg) {
      log( "INVOKING HANDLER ON " + msg );
      receivedMessage  = msg;
      client.disconnect( function() { latch.countDown(); } );
    } );
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.subscriptions.size(), 0 );
    
    subscription = connection.subscriptions.get( subscription_id );
    Assert.assertEquals( "/queues/one", subscription.destination );
    
    sink = connection.messageSink;
    log( "message sink: " + sink );
    stompMessage = org.projectodd.stilts.stomp.StompMessages.createStompMessage( "/queues/one", "this is a sent message" );
    stompMessage.headers.put( "subscription", subscription_id );
    
    sink.send( stompMessage, null );
    pause();
  } );
  
  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );
  
  Assert.assertEquals( "/queues/one", receivedMessage.headers.destination );
  Assert.assertEquals( "this is a sent message", receivedMessage.body );
} );

it( "should be able to receive UTF8 messages", function() {
  client = new Stomp.Client( "localhost", 8675 );
  client.debug = log;
  
  var receivedMessage = null;

  var latch = new java.util.concurrent.CountDownLatch( 1 );
  client.connect( function(frame) {
    subscription_id = client.subscribe("/queues/one", function(msg) {
      log( "INVOKING HANDLER ON " + msg );
      receivedMessage  = msg;
      client.disconnect( function() { latch.countDown(); } );
    } );
    pause();
    
    connection = server.stompProvider.connections.get(0);
    Assert.assertEquals( 1, connection.subscriptions.size(), 0 );
    
    subscription = connection.subscriptions.get( subscription_id );
    Assert.assertEquals( "/queues/one", subscription.destination );
    
    sink = connection.messageSink;
    log( "message sink: " + sink );
    stompMessage = org.projectodd.stilts.stomp.StompMessages.createStompMessage( "/queues/one", "ääbb" );
    stompMessage.headers.put( "subscription", subscription_id );
    
    sink.send( stompMessage, null );
    pause();
  } );
  
  Assert.assertEquals( latch.await( 30, java.util.concurrent.TimeUnit.SECONDS ), true );
  
  Assert.assertEquals( "/queues/one", receivedMessage.headers.destination );
  log( "received message body '" + receivedMessage.body + "'" );
  Assert.assertEquals( "ääbb", receivedMessage.body );
} );
