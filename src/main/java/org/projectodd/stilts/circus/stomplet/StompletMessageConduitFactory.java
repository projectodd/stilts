package org.projectodd.stilts.circus.stomplet;

import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public class StompletMessageConduitFactory implements MessageConduitFactory {

    private StompletContainer container;

    public StompletMessageConduitFactory(StompletContainer container) {
        this.container = container;
    }
    
    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return new StompletMessageConduit( this.container, messageSink );
    }

}
