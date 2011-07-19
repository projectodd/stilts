package org.projectodd.stilts.stomp.client.protocol.websockets;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;

public class DownstreamCaptureHandler implements ChannelDownstreamHandler {

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        this.events.add( e );
    }
    
    public List<ChannelEvent> getEvents() {
        return this.events;
    }
    
    private List<ChannelEvent> events = new ArrayList<ChannelEvent>();

}
