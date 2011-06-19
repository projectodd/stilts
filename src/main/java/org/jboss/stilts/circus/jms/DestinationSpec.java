package org.jboss.stilts.circus.jms;

import javax.jms.Destination;

public class DestinationSpec {
    
    private Destination destination;
    private String selector;

    public DestinationSpec(Destination destination, String selector) {
        this.destination = destination;
        this.selector = selector;
    }
    
    public Destination getDestination() {
        return this.destination;
    }

    public String getSelector() {
        return this.selector;
    }
}
