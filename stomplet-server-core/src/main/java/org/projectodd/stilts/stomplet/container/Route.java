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

package org.projectodd.stilts.stomplet.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.projectodd.stilts.stomplet.Stomplet;

public class Route {

    private static final Pattern SEGMENT_PATTERN = Pattern.compile( ":([a-zA-Z0-9_\\-]+)" );

    public Route(String pattern, Stomplet stomplet) {
        this.pattern = pattern;
        this.stomplet = stomplet;

        setUpRegexp();
    }

    protected void setUpRegexp() {
        Matcher segmentMatcher = SEGMENT_PATTERN.matcher( this.pattern );

        StringBuilder newPattern = new StringBuilder();
        List<String> segmentNames = new ArrayList<String>();

        int end = 0;
        while (segmentMatcher.find()) {
            newPattern.append( this.pattern.substring( end, segmentMatcher.start() ) );
            newPattern.append( "([^\\/]+)" );
            end = segmentMatcher.end();
            String segmentName = segmentMatcher.group( 1 );
            segmentNames.add( segmentName );
        }

        newPattern.append( this.pattern.substring( end ) );

        this.regexp = Pattern.compile( newPattern.toString() );
        this.segmentNames = segmentNames.toArray( new String[segmentNames.size()] );
    }

    public StompletActivator match(String destination) {
        Matcher routeMatcher = this.regexp.matcher( destination );

        if (routeMatcher.matches()) {
            Map<String, String> matches = new HashMap<String, String>();

            for (int i = 0; i < this.segmentNames.length; ++i) {
                matches.put( segmentNames[i], routeMatcher.group( i + 1 ) );
            }
            return new StompletActivator( this, destination, matches );
        }

        return null;
    }

    public String getPatternString() {
        return this.pattern;
    }

    public Stomplet getStomplet() {
        return this.stomplet;
    }

    public String toString() {
        return "[Route: pattern=" + this.pattern + "; stomplet=" + this.stomplet + "]";
    }

    private String pattern;
    private Stomplet stomplet;
    private Pattern regexp;
    private String[] segmentNames;
}
