
var ws = new WebSocket( "ws://localhost:8675/" );

ws.onclose = function() {
  alert( "EVENT closed" );
}

ws.onopen = function() { 
  alert( "EVENT opened" );
  ws.close( 100 );
}

ws.waitForClosedState();