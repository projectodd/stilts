package org.jboss.stilts.circus.xa;

import org.jboss.stilts.circus.MessageConduit;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

public abstract class AbstractXAMessageConduitFactory implements XAMessageConduitFactory {

    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return createXAMessageConduit( messageSink );
    }

}
