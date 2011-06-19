package org.jboss.stilts.spi;

import org.jboss.stilts.StompException;

public interface XAStompProvider extends StompProvider {
    XAStompConnection createXAConnection(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException;
}
