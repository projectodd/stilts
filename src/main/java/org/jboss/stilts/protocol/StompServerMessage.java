package org.jboss.stilts.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.stilts.spi.Headers;

public interface StompServerMessage {
    
    Headers getHeaders();
    String getDestination();
    String getContentType();
    ChannelBuffer getContentBuffer();
}
