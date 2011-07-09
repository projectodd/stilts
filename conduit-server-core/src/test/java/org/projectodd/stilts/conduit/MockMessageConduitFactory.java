package org.projectodd.stilts.conduit;

import org.projectodd.stilts.conduit.spi.MessageConduit;
import org.projectodd.stilts.conduit.spi.MessageConduitFactory;
import org.projectodd.stilts.stomp.Headers;
import org.projectodd.stilts.stomp.spi.AcknowledgeableMessageSink;

public class MockMessageConduitFactory implements MessageConduitFactory {

    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink, Headers headers) throws Exception {
        return new MockMessageConduit();
    }

}
