

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

var Assert = {
  assertEquals: function(expected, actual) {
    if ( expected != actual ) {
      Assert.fail( "Expected: " + expected + ", was: " + actual );
    }
  },
  
  fail: function(msg) {
    org.junit.Assert.fail( msg );
  },
};

//var Assert = org.junit.Assert;

var WebSocket = org.projectodd.stilts.stomp.client.js.websockets.InstrumentedWebSocket;

var log = function(msg) {
  java.lang.System.err.println( "TEST: " + msg );
};

var alert = log;
var console = {
  log: alert,
  debug: alert
};

// Some stubs needed to get swfobject loading in the tests
var document = {};
var navigator = {
  'userAgent': '',
  'platform': '',
};
