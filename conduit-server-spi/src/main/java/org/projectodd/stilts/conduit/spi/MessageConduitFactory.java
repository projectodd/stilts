package org.projectodd.stilts.conduit.spi;

import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public interface MessageConduitFactory {

    MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception;
}
