// (c) 2010 Jeff Mesnil -- http://jmesnil.net/

(function(window) {
  
  var Stomp = {};

  Stomp.frame = function(command, headers, body) {
    return {
      command: command,
      headers: headers,
      body: body,
      toString: function() {
        var out = command + '\n';
        if (headers) {
          for (header in headers) {
            if(headers.hasOwnProperty(header)) {
              out = out + header + ':' + headers[header] + '\n';
            }
          }
        }
        if ( body ) {
            out = out + 'content-length:' + body.length + '\n';
        }
        out = out + '\n';
        if (body) {
          out = out + body;
        }
        return out;
      }
    }
  };

  trim = function(str) {
    return ("" + str).replace(/^\s+/g,'').replace(/\s+$/g,'');
  };

  Stomp.unmarshal = function(data) {
    var divider = data.search(/\n\n/),
        headerLines = data.substring(0, divider).split('\n'),
        command = headerLines.shift(),
        headers = {},
        body = '';

    // Parse headers
    var line = idx = null;
    for (var i = 0; i < headerLines.length; i++) {
      line = headerLines[i];
      idx = line.indexOf(':');
      headers[trim(line.substring(0, idx))] = trim(line.substring(idx + 1));
    }
    try {
	    if (headers['content_length']) {
	    	body = data.substring(divider + 2, parseInt(headers['content_length']));
	    } 
	    else {
	        // Parse body, stopping at the first \0 found.
	    	var chr = null;
		    for (var i = divider + 2; i < data.length; i++) {
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
  };

  Stomp.marshal = function(command, headers, body) {
    return Stomp.frame(command, headers, body).toString() + '\0';
  };
  
  Stomp.client = function (url){

    var that, ws, login, passcode;
    var counter = 0; // used to index subscribers
    // subscription callbacks indexed by subscriber's ID
    var subscriptions = {};

    debug = function(str) {
      if (that.debug) {
        that.debug(str);
      }
    };

    onmessage = function(evt) {
      debug('<<< ' + evt.data);
      var frame = Stomp.unmarshal(evt.data);
      debug('<<< FRAME [' + frame.command + ']' );
      debug('<<< FRAME ' + ( frame.command == 'CONNECTED' ) );
      if (frame.command == "CONNECTED" && that.connectCallback) {
        debug( "calling callback" );
        that.connectCallback(frame);
      } else if (frame.command === "MESSAGE") {
        var onreceive = subscriptions[frame.headers.subscription];
        if (onreceive) {
          onreceive(frame);
        }
      } else if (frame.command === "RECEIPT" && that.onreceipt) {
        that.onreceipt(frame);
      } else if (frame.command === "ERROR" && that.onerror) {
        that.onerror(frame);
      }
    };

    transmit = function(command, headers, body) {
      var out = Stomp.marshal(command, headers, body);
      debug(">>> " + out);
      ws.send(out);
    };

    that = {};

    that.connect = function(login_, passcode_, connectCallback, errorCallback) {
      debug("Opening Web Socket...");
      ws = new WebSocket(url);
      ws.onmessage = onmessage;
      ws.onclose   = function() {
        var msg = "Whoops! Lost connection to " + url;
        debug(msg);
          if (errorCallback) {
            errorCallback(msg);
          }
      };
      ws.onopen    = function() {
        debug('Web Socket Opened...');
        transmit("CONNECT", {login: login, passcode: passcode});
        // connectCallback handler will be called from onmessage when a CONNECTED frame is received
      };
      login = login_;
      passcode = passcode_;
      that.connectCallback = connectCallback;
    };

    that.disconnect = function(disconnectCallback) {
      transmit("DISCONNECT");
      ws.close();
      if (disconnectCallback) {
        disconnectCallback();
      }
    };

    that.waitForDisconnect = function() {
      ws.waitForClosedState();
    };

    that.send = function(destination, headers, body) {
      var headers = headers || {};
      headers.destination = destination;
      transmit("SEND", headers, body);
    };

    that.subscribe = function(destination, callback, headers) {
      var headers = headers || {};
      var id = "sub-" + counter++;
      headers.destination = destination;
      headers.id = id;
      subscriptions[id] = callback;
      transmit("SUBSCRIBE", headers);
      return id;
    };

    that.unsubscribe = function(id, headers) {
      var headers = headers || {};
      headers.id = id;
      delete subscriptions[id];
      transmit("UNSUBSCRIBE", headers);
    };
    
    that.begin = function(transaction, headers) {
      var headers = headers || {};
      headers.transaction = transaction;
      transmit("BEGIN", headers);
    };

    that.commit = function(transaction, headers) {
      var headers = headers || {};
      headers.transaction = transaction;
      transmit("COMMIT", headers);
    };
    
    that.abort = function(transaction, headers) {
      var headers = headers || {};
      headers.transaction = transaction;
      transmit("ABORT", headers);
    };
    
    that.ack = function(message_id, headers) {
      var headers = headers || {};
      headers["message-id"] = message_id;
      transmit("ACK", headers);
    };
    return that;
  };
  
  window.Stomp = Stomp;

})(window);

