package org.projectodd.stilts.circus.xa;

import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public interface XAMessageConduitFactory extends MessageConduitFactory {

    XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception;
}
