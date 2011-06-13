package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.stilts.spi.StompServer;

public interface StompServerChannelHandler extends ChannelHandler {
    
    void setServer(StompServer server);
    void initialize();

}
