
var connected = false;

client = Stomp.client( "ws://localhost:8675/" );

client.connect( null, null, function(frame) {
  connected = true;
  client.send("/queues/one", {priority: 9}, "content 1");
  client.disconnect();
} );

client.waitForDisconnect();
Assert.assertTrue( connected );

connection = server.stompProvider.connections.get(0);
Assert.assertNotNull( connection );

Assert.assertEquals( 1, connection.sends.size(), 0 );

send = connection.sends.get( 0 );
Assert.assertNull( send.transactionId );

message = send.message;
Assert.assertNotNull( message );

Assert.assertEquals( "/queues/one", message.destination );
Assert.assertEquals( "content 1", message.contentAsString );

"completed";

