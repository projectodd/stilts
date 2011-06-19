package org.jboss.stilts.circus.stomplet;

import org.jboss.stilts.circus.MessageConduit;
import org.jboss.stilts.circus.MessageConduitFactory;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

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
