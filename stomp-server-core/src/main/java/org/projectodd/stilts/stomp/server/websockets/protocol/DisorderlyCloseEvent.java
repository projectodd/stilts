package org.projectodd.stilts.stomp.server.websockets.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;

public class DisorderlyCloseEvent implements ChannelEvent {

    public DisorderlyCloseEvent(Channel channel) {
        this.channel = channel;
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return null;
    }
    
    public String toString() {
        return "[DisorderlyCloseEvent]";
    }
    
    private Channel channel;

}
