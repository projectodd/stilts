package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.stilts.protocol.server.ServerCommandPipeline;
import org.jboss.stilts.protocol.transport.BufferTransportPipeline;
import org.jboss.stilts.spi.StompServer;

public class StompPipelineFactory implements ChannelPipelineFactory {

    private Class<? extends ChannelHandler> transportHandlerClass = BufferTransportPipeline.class;
    private Class<? extends StompServerChannelHandler> commandHandlerClass = ServerCommandPipeline.class;

    public StompPipelineFactory(StompServer server) {
        this.server = server;
    }
    
    public void setTransportHandler(Class< ? extends ChannelHandler> handlerClass) {
        this.transportHandlerClass = handlerClass;
    }
    
    public void setCommandHandler(Class<? extends StompServerChannelHandler> handlerClass) {
        this.commandHandlerClass = handlerClass;
    }
    

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        StompPipeline pipeline = new StompPipeline( this.server );
        
        if ( this.transportHandlerClass != null ) {
            ChannelHandler handler = (ChannelHandler) this.transportHandlerClass.newInstance();
            pipeline.addLast( "server-transport", handler );
        }
        
        if ( this.commandHandlerClass != null ) {
            StompServerChannelHandler handler = (StompServerChannelHandler) this.transportHandlerClass.newInstance();
            handler.setServer( this.server );
            pipeline.addLast( "server-command", handler );
        }
        
        return pipeline;
        
    }

    private StompServer server;

}
