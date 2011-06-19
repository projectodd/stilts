package org.projectodd.stilts.spi;

import java.util.Set;

public interface Headers {

    Set<String> getHeaderNames();
	String get(String headerName);
	String put(String headerName, String headerValue);
    void remove(String transaction);
	
	Headers duplicate();
}
