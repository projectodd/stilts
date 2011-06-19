package org.jboss.stilts.circus.xa.psuedo;

import org.jboss.stilts.circus.MessageConduit;
import org.jboss.stilts.circus.MessageConduitFactory;
import org.jboss.stilts.circus.xa.XAMessageConduit;
import org.jboss.stilts.circus.xa.XAMessageConduitFactory;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;

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
