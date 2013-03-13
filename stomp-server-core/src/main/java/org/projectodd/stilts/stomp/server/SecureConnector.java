package org.projectodd.stilts.stomp.server;

import java.net.InetSocketAddress;

import javax.net.ssl.SSLContext;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.projectodd.stilts.stomp.Constants;
import org.projectodd.stilts.stomp.server.protocol.StompServerPipelineFactory;

public class SecureConnector extends AbstractConnector {

    public SecureConnector(SSLContext sslContext) {
        super( new InetSocketAddress( Constants.DEFAULT_SECURE_PORT ) );
        this.sslContext = sslContext;
    }
    
    public SecureConnector(InetSocketAddress socketAddress, SSLContext sslContext) {
        super( socketAddress );
        this.sslContext = sslContext;
    }
    
    @Override
    protected ChannelPipelineFactory getChannelPipelineFactory() {
        return new StompServerPipelineFactory( getServer().getStompProvider(), getServer().getMessageHandlingExecutor(), this.sslContext );
    }

    private SSLContext sslContext;


}
