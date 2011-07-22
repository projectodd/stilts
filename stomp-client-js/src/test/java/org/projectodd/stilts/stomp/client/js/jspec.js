

var tests = [];

var it = function(description, body) {
  tests.push( { description: description, body: body } );
};

var Assert    = org.junit.Assert;
var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket;
var log = function(msg) {
  java.lang.System.err.println( "TEST: " + msg );
};

var alert = log;
