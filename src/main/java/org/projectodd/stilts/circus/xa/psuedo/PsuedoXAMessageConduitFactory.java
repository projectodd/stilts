package org.projectodd.stilts.circus.xa.psuedo;

import org.projectodd.stilts.circus.MessageConduit;
import org.projectodd.stilts.circus.MessageConduitFactory;
import org.projectodd.stilts.circus.xa.XAMessageConduit;
import org.projectodd.stilts.circus.xa.XAMessageConduitFactory;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;

public class PsuedoXAMessageConduitFactory implements XAMessageConduitFactory {

    private MessageConduitFactory factory;

    public PsuedoXAMessageConduitFactory(MessageConduitFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public MessageConduit createMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        return this.factory.createMessageConduit( messageSink );
    }

    @Override
    public XAMessageConduit createXAMessageConduit(AcknowledgeableMessageSink messageSink) throws Exception {
        PsuedoXAAcknowledgeableMessageSink xaMessageSink = new PsuedoXAAcknowledgeableMessageSink( messageSink );
        MessageConduit conduit = createMessageConduit( xaMessageSink );
        PsuedoXAResourceManager resourceManager = new PsuedoXAResourceManager( conduit );
        xaMessageSink.setResourceManager( resourceManager );
        return new PsuedoXAMessageConduit( resourceManager );
    }
}
