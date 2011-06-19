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

package org.projectodd.stilts.protocol;

import static org.projectodd.stilts.protocol.StompFrame.Header.*;

import java.util.Set;

import org.projectodd.stilts.helpers.DefaultHeaders;
import org.projectodd.stilts.protocol.StompFrame.Command;
import org.projectodd.stilts.spi.Headers;

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
