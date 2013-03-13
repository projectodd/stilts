package org.projectodd.stilts.stomp.server;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.server.protocol.StompServerPipelineFactory;

public class InsecureConnector extends AbstractConnector {

    public InsecureConnector() {
        this( new InetSocketAddress( Constants.DEFAULT_PORT ) );
    }

    public InsecureConnector(InetSocketAddress socketAddress) {
        super( socketAddress );
    }
    
    @Override
    protected ChannelPipelineFactory getChannelPipelineFactory() {
        return new StompServerPipelineFactory( getServer().getStompProvider(), getServer().getMessageHandlingExecutor(), null );
    }

}
