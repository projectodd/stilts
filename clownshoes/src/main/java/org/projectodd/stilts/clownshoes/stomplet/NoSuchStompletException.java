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

package org.projectodd.stilts.clownshoes.stomplet;

import org.projectodd.stilts.StompException;

public class NoSuchStompletException extends StompException {

    private static final long serialVersionUID = 1L;
    
    private String className;

    public NoSuchStompletException(String className) {
        super( "No such stomplet class: " + className );
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
}
