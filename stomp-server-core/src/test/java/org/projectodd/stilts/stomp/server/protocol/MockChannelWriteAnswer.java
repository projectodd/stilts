package org.projectodd.stilts.stomp.server.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.DefaultChannelFuture;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.projectodd.stilts.stomp.protocol.StompFrame;

public class MockChannelWriteAnswer implements Answer<ChannelFuture> {

    private List<StompFrame> writeBuffer = new ArrayList<StompFrame>(10);
    
    public List<StompFrame> getWriteBuffer() {
        return writeBuffer;
    }

    @Override
    public ChannelFuture answer(InvocationOnMock invocation) throws Throwable {
        writeBuffer.add( (StompFrame) invocation.getArguments()[0] );
        return new DefaultChannelFuture( (Channel) invocation.getMock(), false );
    }

}
