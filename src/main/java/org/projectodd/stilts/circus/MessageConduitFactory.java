package org.projectodd.stilts.circus;

import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public interface MessageConduitFactory {
    
    MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception;

}
