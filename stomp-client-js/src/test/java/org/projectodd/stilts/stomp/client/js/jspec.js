

var tests = [];

var load = function(path) {

}

var it = function(description, body) {
  java.lang.System.err.println( "added " + description );
  tests.push( { description: description, body: body } );
  java.lang.System.err.println( "added to " + tests.length );
};

var Assert    = org.junit.Assert;
var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.WebSocket;
