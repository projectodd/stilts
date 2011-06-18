package org.jboss.stilts.xa;

import javax.transaction.xa.XAResource;

import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.ClientAgent;
import org.jboss.stilts.spi.Headers;
import org.jboss.stilts.spi.StompProvider;
import org.jboss.stilts.spi.XAStompProvider;

public class WrappedXAStompProvider implements XAStompProvider {

    private StompProviderResourceManager xaManager;
    private StompProvider provider;

    public WrappedXAStompProvider(StompProvider provider) {
        this.provider = provider;
        this.xaManager = new StompProviderResourceManager(provider);
        System.err.println( this + " // " + xaManager );
    }

    public StompProvider getNonXAStompProvider() {
        return this.provider;
    }

    public XAResource getXAResource() {
        return this.xaManager;
    }

    @Override
    public ClientAgent connect(AcknowledgeableMessageSink messageSink, Headers headers) throws StompException {
        XAAcknowledgeableMessageSink xaMessageSink = new XAAcknowledgeableMessageSink( messageSink );
        return this.provider.connect( xaMessageSink, headers );
    }

    @Override
    public void send(StompMessage message) throws StompException {
        XATransaction tx = StompProviderResourceManager.currentTransaction();
        if (tx != null) {
            tx.addSentMessage( message );
        } else {
            provider.send( message );
        }
    }

}
