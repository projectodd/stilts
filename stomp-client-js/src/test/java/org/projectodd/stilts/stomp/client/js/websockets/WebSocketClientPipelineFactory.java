package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public class WebSocketClientPipelineFactory implements ChannelPipelineFactory {


    public WebSocketClientPipelineFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return new WebSocketClientPipeline( this.host, this.port );
    }

    private String host;
    private int port;
}
