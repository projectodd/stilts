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

package org.projectodd.stilts;

public class InvalidSubscriptionException extends StompException {

    public InvalidSubscriptionException(String subscriptionId) {
        super( "Invalid subscription id: " + subscriptionId );
        this.subscriptionId = subscriptionId;
    }
    
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    private static final long serialVersionUID = 1L;

    private String subscriptionId;

}
