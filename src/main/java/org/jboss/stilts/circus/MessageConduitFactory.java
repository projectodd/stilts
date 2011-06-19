package org.jboss.stilts.circus;

import org.jboss.stilts.spi.AcknowledgeableMessageSink;

public interface MessageConduitFactory {
    
    MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception;

}
