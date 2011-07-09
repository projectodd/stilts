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

package org.projectodd.stilts.stomplet.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StompletConfParser {

    private Pattern ROUTE_PATTERN = Pattern.compile( "^ROUTE ([^\\s]+) ([^\\s]+)$" );
    private BufferedReader reader;

    public StompletConfParser(InputStream in) {
        this.reader = new BufferedReader( new InputStreamReader( in ) );
    }

    public List<RouteConfiguration> parse() throws IOException {
        List<RouteConfiguration> configs = new ArrayList<RouteConfiguration>();

        String line = null;

        while ((line = readLine( true )) != null) {
            RouteConfiguration config = null;
            Matcher matcher = ROUTE_PATTERN.matcher( line );
            if (matcher.matches()) {
                String pattern = matcher.group( 1 );
                String className = matcher.group( 2 );
                config = new RouteConfiguration( pattern, className );
                configs.add( config );

                INNER:
                while ((line = readLine( false )) != null) {
                    if ( "".equals(line) ) {
                        break INNER;
                    }
                    String[] values = line.split( ":" );
                    config.getProperties().put(  values[0], values[1] );
                }
            } else {
                continue;
            }
        }

        return configs;
    }

    protected String readLine() throws IOException {
        return readLine( false );
    }

    protected String readLine(boolean skipBlanks) throws IOException {
        String line = null;

        while ((line = this.reader.readLine()) != null) {
            int hashLoc = line.indexOf( "#" );
            if (hashLoc >= 0) {
                line = line.substring( 0, hashLoc );
            }
            line = line.trim();
            
            if ( ! skipBlanks ) {
                return line;
            }

            if (!line.equals( "" )) {
                return line;
            }
        }

        return line;
    }
}
