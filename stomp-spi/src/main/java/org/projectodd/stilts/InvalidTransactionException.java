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

public class InvalidTransactionException extends StompException {
    
    
    public InvalidTransactionException(String transactionId) {
        super( "Invalid transaction: " + transactionId);
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return this.transactionId;
    }
    
    private static final long serialVersionUID = 1L;
    private String transactionId;

}
