

var alert = function(msg) {
  java.lang.System.err.println( msg );
};

var pause = function(time) {
  if ( ! time ) {
    time = 1000;
  }
  
  java.lang.Thread.sleep( time );
};

var Assert    = org.junit.Assert;
var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket;
