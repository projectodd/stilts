package org.projectodd.stilts.protocol;

import org.jboss.netty.channel.ChannelHandler;
import org.projectodd.stilts.spi.StompProvider;

public interface StompServerChannelHandler extends ChannelHandler {
    
    void setServer(StompProvider server);
    void initialize();

}
