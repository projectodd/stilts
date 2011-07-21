client = window.Stomp.client( "ws://localhost:8675/" );
client.debug = function(msg) { alert( msg ); };
client.connect( null, null, function(frame) {
  alert( "connected! with frame" + frame );
  client.disconnect();
} );

client.waitForDisconnect();

/*
var ws = new WebSocket( "ws://localhost:8675/" );

ws.onclose = function() {
  alert( "EVENT closed" );
}

ws.onopen = function() { 
  alert( "EVENT opened" );
  ws.close( 100 );
}

ws.waitForClosedState();
*/
