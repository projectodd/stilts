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

package org.projectodd.stilts.clownshoes.stomplet;

import java.util.Map;

public class RouteMatch {
    
    public RouteMatch(Route route, String destination, Map<String,String> matches) {
        this.route = route;
        this.destination = destination;
        this.matches = matches;
    }
    
    public Route getRoute() {
        return this.route;
    }
    
    public String getDestination() {
        return this.destination;
    }
    
    public void put(String name, String value) {
        this.matches.put( name, value );
    }
    
    public String get(String name) {
        return this.matches.get( name );
    }
    
    public Map<String,String> getMatches() {
        return this.matches;
    }
    
    public String toString() {
        return "[DefaultRouteMatch: destination=" + this.destination + "; "
            + " matches=" + this.matches 
            + "]";
    }
    
    private Route route;
    private String destination;
    private Map<String, String> matches;


}
