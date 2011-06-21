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

package org.projectodd.stilts.clownshoes.stomplet;

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
        while ( segmentMatcher.find() ) {
            newPattern.append( this.pattern.substring( end, segmentMatcher.start() ));
            newPattern.append( "([^\\/]+)");
            end = segmentMatcher.end();
            String segmentName = segmentMatcher.group( 1 );
            segmentNames.add( segmentName );
        }
        
        newPattern.append(  this.pattern.substring( end ) );
        
        this.regexp = Pattern.compile( newPattern.toString() );
        this.segmentNames = segmentNames.toArray( new String[segmentNames.size()] );
    }
    
    public RouteMatch match(String destination) {
        Matcher routeMatcher = this.regexp.matcher( destination );
        
        if ( routeMatcher.matches() ) {
            Map<String,String> matches = new HashMap<String,String>();
            
            for ( int i = 0 ; i < this.segmentNames.length ; ++i ) {
               matches.put(  segmentNames[i], routeMatcher.group( i+1 ) );
            }
            return new RouteMatch( this, destination, matches );
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
        return "[Route: pattern=" + this.pattern + "]";
    }

    private String pattern;
    private Stomplet stomplet;
    private Pattern regexp;
    private String[] segmentNames;
}
