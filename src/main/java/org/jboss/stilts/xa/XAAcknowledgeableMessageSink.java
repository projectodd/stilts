package org.jboss.stilts.xa;

import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;
import org.jboss.stilts.spi.XAStompProvider;

public class XAAcknowledgeableMessageSink implements AcknowledgeableMessageSink {

    private AcknowledgeableMessageSink sink;

    public XAAcknowledgeableMessageSink(AcknowledgeableMessageSink sink) {
        this.sink = sink;
    }
    
    @Override
    public void send(StompMessage message) throws StompException {
        this.sink.send( message );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        Acknowledger xaAcknowledger = new XAAcknowledger( acknowledger );
        this.sink.send( message, xaAcknowledger );
    }

}
