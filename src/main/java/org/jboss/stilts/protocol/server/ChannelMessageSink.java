package org.jboss.stilts.protocol.server;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;

public class ChannelMessageSink implements AcknowledgeableMessageSink {

    public ChannelMessageSink(Channel channel, AckManager ackManager) {
        this.channel = channel;
        this.ackManager = ackManager;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        send( message, null );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        if ( message.getId() == null ) {
            message.getHeaders().put( Header.MESSAGE_ID, getNextMessageId() );
        }
        if (acknowledger != null) {
            this.ackManager.registerAcknowledger( message.getId(), acknowledger );
        }
        this.channel.write( message );
    }
    
    private static String getNextMessageId() {
        return "message-" + MESSAGE_COUNTER.getAndIncrement();
    }

    private Channel channel;
    private AckManager ackManager;
    
    private static final AtomicLong MESSAGE_COUNTER = new AtomicLong();

}
