package org.jboss.stilts.circus.xa;

import org.jboss.stilts.circus.MessageConduitFactory;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

public interface XAMessageConduitFactory extends MessageConduitFactory {

    XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception;
}
