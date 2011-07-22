

var tests = [];

var it = function(description, body) {
  tests.push( { description: description, body: body } );
};

var pause = function(time) {
  if ( ! time ) {
    time = 100;
  }
  
  java.lang.Thread.sleep( time );
};

var Assert    = org.junit.Assert;
var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket;

var log = function(msg) {
  java.lang.System.err.println( "TEST: " + msg );
};

var alert = log;
