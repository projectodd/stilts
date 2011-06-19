package org.projectodd.stilts.circus.xa.psuedo;

import org.projectodd.stilts.StompException;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.spi.AcknowledgeableMessageSink;
import org.projectodd.stilts.spi.Acknowledger;

public class PsuedoXAAcknowledgeableMessageSink implements AcknowledgeableMessageSink {

    public PsuedoXAAcknowledgeableMessageSink(AcknowledgeableMessageSink sink) {
        this.sink = sink;
    }
    
    void setResourceManager(PsuedoXAResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.sink.send( message );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        Acknowledger xaAcknowledger = new PsuedoXAAcknowledger( this.resourceManager, acknowledger );
        this.sink.send( message, xaAcknowledger );
    }
    
    private PsuedoXAResourceManager resourceManager;
    private AcknowledgeableMessageSink sink;

}
