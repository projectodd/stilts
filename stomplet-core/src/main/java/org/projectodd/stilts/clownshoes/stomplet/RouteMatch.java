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
