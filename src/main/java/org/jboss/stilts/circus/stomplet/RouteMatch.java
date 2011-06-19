package org.jboss.stilts.circus.stomplet;

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
