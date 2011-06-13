package org.jboss.stilts.stomplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.Headers;

public class StompletMessageRouter implements MessageRouter {

    public StompletMessageRouter() {

    }

    @Override
    public void send(StompMessage message) throws StompException {
        RouteMatch match = match( message.getDestination() );

        if (match != null) {
            Stomplet stomplet = match.getRoute().getStomplet();
            Map<String, String> matches = match.getMatches();
            Headers headers = message.getHeaders();
            for (String name : matches.keySet()) {
                headers.put( "stomplet." + name, matches.get( name ) );
            }
            stomplet.onMessage( this, message );
        }
    }

    public void addRoute(String destinationPattern, Stomplet stomplet) {
        this.routes.add( new Route( destinationPattern, stomplet ) );
    }

    RouteMatch match(String destination) {
        RouteMatch match = null;
        for (Route route : this.routes) {
            match = route.match( destination );
            if (match != null) {
                break;
            }
        }

        return match;
    }

    private List<Route> routes = new ArrayList<Route>();

}
