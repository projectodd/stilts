package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;

public interface StompProvider {
    StompConnection createConnection(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException;
}
