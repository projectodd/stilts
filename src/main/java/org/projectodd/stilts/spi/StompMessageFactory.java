package org.projectodd.stilts.spi;

import org.jboss.netty.buffer.ChannelBuffer;
import org.projectodd.stilts.StompMessage;

public interface StompMessageFactory {
    
    StompMessage createMessage(Headers headers, ChannelBuffer channelBuffer, boolean isError);
}
