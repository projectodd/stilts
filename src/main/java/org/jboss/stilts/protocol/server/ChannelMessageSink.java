package org.jboss.stilts.protocol.server;

import org.jboss.netty.channel.Channel;
import org.jboss.stilts.MessageSink;
import org.jboss.stilts.StompMessage;

public class ChannelMessageSink implements MessageSink {

    public ChannelMessageSink(Channel channel) {
        this.channel = channel;
    }
    
    @Override
    public void send(StompMessage message) {
        this.channel.write( message );
    }

    private Channel channel;

}
