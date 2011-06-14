package org.jboss.stilts.spi;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;

public interface StompProvider {
    
    ClientAgent connect(MessageSink messageSink, Headers headers) throws StompException;

}
