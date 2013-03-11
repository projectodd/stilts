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
  
  connect: function(callback) {
    var headers = {};
    if ( this._login ) {
      headers.login = this._login;
    }
    if ( this._passcode ) {
      headers.passcode = this._passcode;
    }
    
    headers[Stomp.Headers.ACCEPT_VERSION] = this.client.supportedVersions();
    
    try {
      this.transmitSync( "CONNECT", headers );
    } catch (err) {
      return false;
    }
    
    this.connectMessageReceiver();
    
    if ( callback ) {
      callback();
    }
    
    return true;
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
  
};

Stomp.Transports.push( Stomp.Transport.LongPoll );