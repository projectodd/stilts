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

package org.projectodd.stilts.circus.jms;

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
