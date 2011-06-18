package org.jboss.stilts.spi;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.StompMessage;

public interface StompMessageFactory {
    
    StompMessage createMessage(Headers headers, ChannelBuffer channelBuffer, boolean isError);
}
