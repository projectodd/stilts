/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.protocol;

import static org.projectodd.stilts.stomp.protocol.StompFrame.Header.CONTENT_LENGTH;

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
