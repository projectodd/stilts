
Stomp.Transport.WebSocket = function(host, port, secure) {
  this._host = host;
  this._port = port;
  this._secure = secure;
}

Stomp.Transport.WebSocket.prototype = {
  _ws: undefined,
  _state: 'unconnected',
  
  transmit: function(command, headers, body) {
    var out = Stomp.marshal(command, headers, body);
    this._ws.send(out);
  },
  
  connect: function(callback, errorCallback) {
    var wsClass = null;
    if ( typeof WebSocket != 'undefined' ) {
      wsClass = WebSocket;
    } else if ( typeof MozWebSocket != 'undefined' ) {
      wsClass = MozWebSocket;
    } else {
      return;
    }
    
    this._connectCallback = callback;
    this._ws = new wsClass( this._url() );
    this._ws.onopen = this._issueConnect.bind(this);
    this._ws.onmessage = this._handleMessage.bind(this);
    this._ws.onerror = errorCallback;
  },
  
  close: function() {
    this._ws.close(); 
  },
  
  send: function(data) {
    this._ws.send(data);
  },
  
  _issueConnect: function() {
    var headers = {};
    this._ws.onerror = this.client.onerror;
    if ( this._login ) {
      headers['login'] = this._login;
    }
    if ( this._passcode ) {
      headers['passcode'] = this._passcode;
    }
    console.debug( this.client );
    headers[Stomp.Headers.ACCEPT_VERSION] = this.client.supportedVersions();
    this.transmit( "CONNECT", headers)
  },
  
  _handleMessage: function(evt) {
    var frame = Stomp.unmarshal(evt.data);
    if (frame.command == "CONNECTED") {
      this._version = frame.headers[Stomp.Headers.VERSION];
      if (this._connectCallback) {
        this._connectCallback(frame);
      }
    } else {
      this.client.processMessage( frame );
    }
  },
  
  _url: function() {
    if ( this._secure ) {
      return "wss://" + this._host + ":" + this._port + "/";
    }
    return "ws://" + this._host + ":" + this._port + "/";
  },
}


Stomp.Transports.push( Stomp.Transport.WebSocket );