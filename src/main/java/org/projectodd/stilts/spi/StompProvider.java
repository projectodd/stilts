package org.projectodd.stilts.spi;

import org.projectodd.stilts.StompException;

public interface StompProvider {
    StompConnection createConnection(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException;
}
