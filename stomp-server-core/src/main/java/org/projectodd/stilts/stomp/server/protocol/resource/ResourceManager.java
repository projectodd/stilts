package org.projectodd.stilts.stomp.server.protocol.resource;

import org.jboss.netty.buffer.ChannelBuffer;

public interface ResourceManager {
    
    ChannelBuffer getResource(String uri);

}
