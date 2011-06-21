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

package org.projectodd.stilts.clownshoes.parser;

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
