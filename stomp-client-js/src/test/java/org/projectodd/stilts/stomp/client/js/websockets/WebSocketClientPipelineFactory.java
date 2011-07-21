package org.projectodd.stilts.stomp.client.js.websockets;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

public class WebSocketClientPipelineFactory implements ChannelPipelineFactory {


    public WebSocketClientPipelineFactory(WebSocket socket, String host, int port) {
        this.socket = socket;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        return new WebSocketClientPipeline( this.socket, this.host, this.port );
    }

    private WebSocket socket;
    private String host;
    private int port;
}
