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

import java.util.Set;

public interface Headers {

    Set<String> getHeaderNames();
	String get(String headerName);
	String put(String headerName, String headerValue);
    void remove(String transaction);
	
	Headers duplicate();
}
