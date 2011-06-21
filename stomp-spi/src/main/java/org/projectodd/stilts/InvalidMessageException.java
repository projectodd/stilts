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

public class InvalidMessageException extends StompException {

    public InvalidMessageException(String messageId) {
        super( "Invalid message id: " + messageId );
        this.messageId = messageId;
    }
    
    public String getMessageId() {
        return this.messageId;
    }

    private static final long serialVersionUID = 1L;

    private String messageId;

}
