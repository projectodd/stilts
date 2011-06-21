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

package org.projectodd.stilts.stomp.spi;

import org.projectodd.stilts.StompException;

public interface Subscription {
    
    public static enum AckMode {
        AUTO("auto"),
        CLIENT("client"),
        CLIENT_INDIVIDUAL("client-individual"),;
        
        private String str;

        AckMode(String str) {
            this.str = str;
        }
        
        public String toString() {
            return this.str;
        }
    }
    
    String getId();
    void cancel() throws StompException;
}
