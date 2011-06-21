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

package org.projectodd.stilts.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.projectodd.stilts.stomp.spi.Headers;

public class DefaultHeaders extends HashMap<String, String> implements Headers {

    private static final long serialVersionUID = 1L;

    @Override
    public String get(String headerName) {
        return super.get( headerName );
    }

    public void putAll(Headers headers) {
        for (String name : headers.getHeaderNames()) {
            put( name, headers.get( name ) );
        }
    }
    
    public void remove(String headerName) {
        super.remove( headerName );
    }

    @Override
    public Set<String> getHeaderNames() {
        return keySet();
    }

    @Override
    public Headers duplicate() {
        DefaultHeaders dupe = new DefaultHeaders();
        dupe.putAll(  (Map<String,String>) this  );
        return dupe;
    }

}
