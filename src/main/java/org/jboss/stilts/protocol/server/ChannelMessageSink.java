package org.jboss.stilts.protocol.server;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.jboss.stilts.StompException;
import org.jboss.stilts.StompMessage;
import org.jboss.stilts.protocol.StompFrame.Header;
import org.jboss.stilts.spi.AcknowledgeableMessageSink;
import org.jboss.stilts.spi.Acknowledger;

public class ChannelMessageSink implements AcknowledgeableMessageSink {

    public ChannelMessageSink(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(StompMessage message) throws StompException {
        send( message, null );
    }

    @Override
    public void send(StompMessage message, Acknowledger acknowledger) throws StompException {
        StompMessage dupe = message.duplicate();
        String messageId = getNextMessageId();
        dupe.getHeaders().put( Header.MESSAGE_ID, messageId );
        this.channel.write( dupe );
    }

    protected String getNextMessageId() {
        return "message-" + this.messageCounter.getAndIncrement();
    }

    private AtomicLong messageCounter = new AtomicLong();
    private Channel channel;

}
