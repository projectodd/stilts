/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.protocol;

import static org.projectodd.stilts.stomp.protocol.StompFrame.Header.*;

import java.util.Set;

import org.projectodd.stilts.helpers.DefaultHeaders;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.spi.Headers;

public class FrameHeader {

    public FrameHeader() {

    }

    public FrameHeader(Command command) {
        this.command = command;
    }
    
    public FrameHeader(Command command, Headers headers) {
        this.command = command;
        this.headers.putAll( headers );
    }
    
    public boolean isContentFrame() {
        return this.command.hasContent();
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }

    public void set(String name, String value) {
        this.headers.put( name.toLowerCase(), value );
    }

    public String get(String name) {
        return this.headers.get( name.toLowerCase() );
    }
    
    public Set<String> getNames() {
        return this.headers.keySet();
    }
    
    public Headers getMap() {
        return this.headers;
    }

    public int getContentLength() {
        String value = get( CONTENT_LENGTH.toString() );
        if (value == null) {
            return -1;
        }

        return Integer.parseInt( value );
    }
    
    public String toString() {
        return "[FrameHeader: command=" + this.command + "; headers=" + this.headers + "]";
    }

    private Command command;
    private DefaultHeaders headers = new DefaultHeaders();
}
