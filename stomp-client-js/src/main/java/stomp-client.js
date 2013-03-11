// Stilts stomp-client.js ${project.version}
// Some parts (c) 2010 Jeff Mesnil -- http://jmesnil.net/

var Stomp = {

  Headers: {
    HOST : 'host',
    CONTENT_LENGTH : 'content-length',
    CONTENT_TYPE : 'content-type',
    ACCEPT_VERSION : 'accept-version',
    VERSION : 'version'
  },

  unmarshal: function(data) {
    var divider     = data.search(/\n\n/);
    var headerLines = data.substring(0, divider).split('\n');
    var command     = headerLines.shift(), headers = {}, body = '';
  
    // Parse headers
    var line = idx = null;
    for ( var i = 0; i < headerLines.length; i++) {
      line = '' + headerLines[i];
      idx = line.indexOf(':');
      headers[line.substring(0, idx).trim()] = line.substring(idx + 1).trim();
    }
    try {
      if (headers[Stomp.Headers.CONTENT_LENGTH]) {
        var len = parseInt( headers[Stomp.Headers.CONTENT_LENGTH] );
        var start = divider + 2;
        body = (''+ data).substring(start, start+len);
      } else {
        // Parse body, stopping at the first \0 found.
        var chr = null;
        for ( var i = divider + 2; i < data.length; i++) {
          chr = data.charAt(i);
          if (chr === '\0') {
            break;
          }
          body += chr;
        }
      }
      return Stomp.frame(command, headers, body);
    } catch (err) {
      return Stomp.frame('ERROR', headers, "Error parsing frame: " + err.description);
    }
  },

  marshal: function(command, headers, body) {
    var frame = Stomp.frame(command, headers, body);
    return frame.toString() + '\0';
  },
  
  frame: function(command, headers, body) {
    return {
      command : command,
      headers : headers,
      body : body,
      toString : function() {
        var out = command + '\n';
        if (headers) {
          for (header in headers) {
            if (headers.hasOwnProperty(header)) {
              out = out + header + ':' + headers[header] + '\n';
            }
          }
        }
        if (body) {
          out = out + 'content-length:' + body.length + '\n';
        }
        out = out + '\n';
        if (body) {
          out = out + body;
        }
        return out;
      }
    }
  },
  
  Transport: {
  
  }
};

Stomp.Transport.WebSocket = function(host, port, secure) {
  this._host = host;
  this._port = port;
  this._secure = secure;
}

Stomp.Transport.WebSocket.prototype = {
  _ws: undefined,
  
  close: function() {
    this._ws.close(); 
  },
  
  send: function(data) {
    this._ws.send(data);
  },
}

// ----------------------------------------
// Long-Poll Transport
// ----------------------------------------

Stomp.Transport.LongPoll = function(host, port, secure) {
  this._host = host;
  this._port = port;
  this._secure = secure;
}

Stomp.Transport.LongPoll.prototype = {

  _receiverRequest: undefined,
  _disconnectReceiver: false,
  
  connect: function() {
    var headers = {};
    if ( this._login ) {
      headers.login = this._login;
    }
    if ( this._passcode ) {
      headers.passcode = this._passcode;
    }
    try {
      this.transmitSync( "CONNECT", headers );
    } catch (err) {
      return;
    }
    
    this.connectMessageReceiver();
    
    return this;
  },

  connectMessageReceiver: function() {
    var transport = this;
  
    var request = new XMLHttpRequest();
    request.open( "GET", this._url(), true );
    request.onload = function() {
      var message = Stomp.unmarshal( request.response );
      transport.client.processMessage( message );
    }
  
    request.onloadend = function() {
      if ( transport._disconnectReceiver ) {
        return;
      }
      transport.connectMessageReceiver();
    }
    
    setTimeout( function() {
      if ( request.readyState != 0 && request.readyState != 4 ) {
        request.abort();
      }
    }, 10000 );
    
    request.setRequestHeader("Content-type","text/stomp-poll");
    request.withCredentials = true;
    request.send();
    this._receiverRequest = request;
  },

  disconnectMessageReceiver: function() {
    this._disconnectReceiver = true;
    this._receiverRequest.abort();
  },

  close: function() {
    this.disconnectMessageReceiver();
  },

  transmitSync: function(command, headers, body, callbacks) {
    var data = Stomp.marshal(command, headers, body);
    this.send(data, callbacks, false);
  },
  
  transmit: function(command, headers, body, callbacks) {
    var data = Stomp.marshal(command, headers, body);
    this.send(data, callbacks);
  },
  
  send: function(data, callbacks, async) {
    callbacks = callbacks || {};
    var request = new XMLHttpRequest();
    request.open( "POST", this._url(), async );
    if ( callbacks['load'] ) {
      request.onload = function() {
        callbacks['load'](request);
      }
    }
    if ( callbacks['error'] ) {
      requrest.onerror = function() {
        callbacks['error'](request);
      }
    }
    request.setRequestHeader("Content-type","text/stomp");
    request.withCredentials = true;
    request.send(data);
  },
  
  _url: function() {
    if ( this._secure ) {
      return "https://" + this._host + ":" + this._port + "/";
    }
    return "http://" + this._host + ":" + this._port + "/";
  },
  
}



Stomp.Client = function(host, port, secure) {
  this._host   = host;
  this._port   = port   || 8675;
  this._secure = secure || false;
}

Stomp.Client.prototype = {

  Versions: { 
    VERSION_1_0 : "1.0", 
    VERSION_1_1 : "1.1",
  
    supportedVersions : function() {
      return "1.0,1.1";
    }
  },
  
  disableWebSocket: function() {
    this._webSocketEnabled = false;
  },
  
  disableLongPoll: function() {
    this._longPollEnabled = false;
  },
  
  connect: function(callback) {
    if ( arguments.length == 1 ) {
      this._connectCallback = arguments[0];
    }
    if ( arguments.length == 2 ) {
      this._connectCallback = arguments[0];
      this._errorCallback = arguments[1];
    }
    if ( arguments.length == 3 ) {
      this._login = arguments[0];
      this._passcode = arguments[1];
      this._connectCallback = arguments[2];
    }
    if ( arguments.length == 4 ) {
      this._login = arguments[0];
      this._passcode = arguments[1];
      this._connectCallback = arguments[2];
      this._errorCallback = arguments[3];
    }
    
    this._connectTransport();
    
    if ( ! this._transport ) {
      return;
    }
    this._transport.client = this;
    callback();
  },
  
  _connectTransport: function() {
    this._transport = this._connectWebSocket();
    if ( ! this._transport ) {
      this._transport = this._connectLongPoll();
    }
  },
  
  _connectWebSocket: function() {
  },
  
  _connectLongPoll: function() {
    var transport = new Stomp.Transport.LongPoll( this._host, this._port, this._secure );
    return transport.connect();
  },
  
  disconnect: function(disconnectCallback) {
    this._transmit("DISCONNECT");
    this._transport.close();
    if (disconnectCallback) {
      disconnectCallback();
    }
  },

  waitForDisconnect: function() {
    this._transport.waitForClosedState();
  },
  
  send: function(destination, headers, body) {
    var headers = headers || {};
    headers.destination = destination;
    this._transmit("SEND", headers, body);
  },
  
  subscribe: function(destination, callback, headers) {
    var headers = headers || {};
    var subscription_id = "sub-" + this._counter++;
    headers.destination = destination;
    headers.id = subscription_id;
    this._subscriptions['' + subscription_id] = callback;
    this._transmit("SUBSCRIBE", headers);
    return subscription_id;
  },
  
  unsubscribe: function(id, headers) {
    var headers = headers || {};
    headers.id = id;
    delete this._subscriptions[id];
    this._transmit("UNSUBSCRIBE", headers);
  },

  begin: function(transaction, headers) {
    var headers = headers || {};
    headers.transaction = transaction;
    this._transmit("BEGIN", headers);
  },

  commit: function(transaction, headers) {
    var headers = headers || {};
    headers.transaction = transaction;
    this._transmit("COMMIT", headers);
  },

  abort: function(transaction, headers) {
    var headers = headers || {};
    headers.transaction = transaction;
    this._transmit("ABORT", headers);
  },

  ack: function(message_id, headers) {
    var headers = headers || {};
    headers["message-id"] = message_id;
    this._transmit("ACK", headers);
  },
		
  nack: function(message_id, headers) {
    // TODO: Add nack functionality.
  },
  
  // ----------------------------------------
  processMessage: function(message) {
    var subId = message.headers['subscription'];
    var callback = this._subscriptions[ subId ];
    callback(message);
  },
  // ----------------------------------------
  
  _login: undefined,
  _passcode: undefined,
  _connectCallback: undefined,
  _errorCallback: undefined,
  _webSocketEnabled: true,
  _longPollEnabled: true,
  
  _transport: undefined,
  _subscriptions: {},
  _counter: 0,
  
  _transmit: function(command, headers, body) {
	this._transport.transmit(command, headers, body);
  },
  
  
}