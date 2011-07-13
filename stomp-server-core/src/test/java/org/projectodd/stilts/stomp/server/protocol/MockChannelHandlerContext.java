package org.projectodd.stilts.stomp.server.protocol;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;

public class MockChannelHandlerContext implements ChannelHandlerContext {

    private Object attachment;
    
    private Channel channel;
    
    private ChannelHandler handler;
    
    public MockChannelHandlerContext(Channel channel, ChannelHandler handler) {
        this.channel = channel;
        this.handler = handler;
    }
    
    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public ChannelPipeline getPipeline() {
        return channel.getPipeline();
    }

    @Override
    public String getName() {
        return "MockChannelHandlerContext";
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public boolean canHandleUpstream() {
        return true;
    }

    @Override
    public boolean canHandleDownstream() {
        return true;
    }

    @Override
    public void sendUpstream(ChannelEvent e) {
        channel.getPipeline().sendUpstream( e );
    }

    @Override
    public void sendDownstream(ChannelEvent e) {
        channel.getPipeline().sendDownstream( e );
    }

    @Override
    public Object getAttachment() {
        return attachment;
    }

    @Override
    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

}
