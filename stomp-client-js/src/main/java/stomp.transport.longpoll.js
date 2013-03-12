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
  
  connect: function(callback, errorCallback) {
    var headers = {};
    if ( this._login ) {
      headers.login = this._login;
    }
    if ( this._passcode ) {
      headers.passcode = this._passcode;
    }
    
    headers[Stomp.Headers.ACCEPT_VERSION] = this.client.supportedVersions();
    
    var transport = this;
    
    var request = new XMLHttpRequest();
    request.open( "POST", this._url(), true );
    request.withCredentials = true;
    
    var timeoutHandle = setTimeout( function() {
      if ( request.readyState != 0 && request.readyState != 4 ) {
        request.abort();
        errorCallack();
      }
    }, 5000 );
    
    request.onerror = errorCallback;
    
    request.onload = function() {
      clearTimeout( timeoutHandle );
      transport.connectMessageReceiver();
      callback();
    }
    request.setRequestHeader("Content-type","text/stomp");
    
    var data = Stomp.marshal("CONNECT", headers);
    request.send(data);
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

  transmit: function(command, headers, body, callbacks, timeoutMs) {
    var data = Stomp.marshal(command, headers, body);
    this.send(data, callbacks, timeoutMs);
  },
  
  send: function(data, callbacks) {
    callbacks = callbacks || {};
    var request = new XMLHttpRequest();
    request.open( "POST", this._url(), true );
    request.withCredentials = true;
    console.debug( request );
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