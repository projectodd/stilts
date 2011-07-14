package org.projectodd.stilts.stomp.server.websockets.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;

public class HostDecodedEvent implements ChannelEvent {

    public HostDecodedEvent(Channel channel, String host) {
        this.channel = channel;
        this.host = host;
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ChannelFuture getFuture() {
        return null;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String toString() {
        return "[HostDecodedEvent: " + this.host + "]";
    }
    
    private Channel channel;
    private String host;

}
