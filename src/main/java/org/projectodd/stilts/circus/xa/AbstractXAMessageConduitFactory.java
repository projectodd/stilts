package org.projectodd.stilts.circus.xa;

import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public abstract class AbstractXAMessageConduitFactory implements XAMessageConduitFactory {

    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return createXAMessageConduit( messageSink );
    }

}
