/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.projectodd.stilts.stomp.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.projectodd.stilts.spi.Headers;

/**
 * A base STOMP frame.
 * 
 * @author Bob McWhirter
 */
public class StompFrame {

    public static class Header {
        public static final String CONTENT_LENGTH = "content-length";
        public static final String CONTENT_TYPE = "content-type";
        public static final String SESSION = "session";
        public static final String DESTINATION = "destination";
        public static final String ID = "id";
        public static final String RECEIPT = "receipt";
        public static final String RECEIPT_ID = "receipt-id";
        public static final String ACK = "ack";
        public static final String SELECTOR = "selector";
        public static final String TRANSACTION = "transaction";
        public static final String SUBSCRIPTION = "subscription";
        public static final String MESSAGE_ID = "message-id";
    }

    public static class Command {
        public static Map<String,Command> commands = new HashMap<String,Command>();
        
        public static Command valueOf(String text) {
            text = text.toLowerCase();
            Command c = Command.commands.get( text );
            return c;
        }
        
        private String name;
        private boolean hasContent;
        public Command(String name, boolean hasContent) {
            this.name = name;
            this.hasContent = hasContent;
            Command.commands.put( name.toLowerCase(), this );
        }
        
        public boolean hasContent() {
            return this.hasContent;
        }
        
        public byte[] getBytes() {
            return this.name.getBytes();
        }
        
        public String toString() {
            return this.name;
        }
        
        public boolean equals(Object o) {
            return toString().equals( o.toString() );
        }
        
        public static final Command STOMP = new Command("STOMP", false );
        public static final Command CONNECT = new Command("CONNECT", false );
        public static final Command CONNECTED = new Command("CONNECTED", false);
        public static final Command DISCONNECT = new Command("DISCONNECT", false);
        
        public static final Command SEND = new Command("SEND", true);
        public static final Command MESSAGE = new Command("MESSAGE", true);
        
        public static final Command SUBSCRIBE = new Command("SUBSCRIBE", false);
        public static final Command UNSUBSCRIBE = new Command("UNSUBSCRIBE", false);
        
        public static final Command BEGIN = new Command("BEGIN", false);
        public static final Command COMMIT = new Command("COMMIT", false);
        public static final Command ACK = new Command("ACK", false);
        public static final Command NACK = new Command("NACK", false);
        public static final Command ABORT = new Command("ABORT", false);
        
        public static final Command RECEIPT = new Command("RECEIPT", false);
        public static final Command ERROR = new Command("ERROR", true);

    }

    /**
     * Create a new outbound frame.
     * 
     * @param command
     */
    public StompFrame(Command command) {
        this.header = new FrameHeader( command );
    }
    
    public StompFrame(Command command, Headers headers) {
        this.header = new FrameHeader( command, headers );
    }
    
    public StompFrame(FrameHeader header) {
        this.header = header;
    }
    

    public Command getCommand() {
        return this.header.getCommand();
    }

    public String getHeader(String name) {
        return this.header.get( name );
    }

    public void setHeader(String name, String value) {
        this.header.set( name, value );
    }
    
    public Set<String> getHeaderNames() {
        return this.header.getNames();
    }
    
    public Headers getHeaders() {
        return this.header.getMap();
    }
    
    public String toString() {
        return "[" + getClass().getSimpleName() + ": header=" + this.header + "]";
    }
    
    private FrameHeader header;

}
