package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.stilts.spi.StompProvider;

public interface StompServerChannelHandler extends ChannelHandler {
    
    void setServer(StompProvider server);
    void initialize();

}
