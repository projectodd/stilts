package org.jboss.stilts.protocol;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.stilts.spi.StompServer;


public class StompPipelineFactoryBuilder {

    StompPipelineFactoryBuilder(StompPipelineFactory factory) {
        this.factory = factory;
    }
    
    public static StompPipelineFactoryBuilder forServer(StompServer server) {
        return new StompPipelineFactoryBuilder( new StompPipelineFactory(server) );
    }
    
    public StompPipelineFactoryBuilder withTransportHandler(Class<? extends ChannelHandler> transportHandlerClass) {
        this.factory.setTransportHandler( transportHandlerClass );
        return this;
    }
    
    public StompPipelineFactoryBuilder withCommandHandler(Class<? extends StompServerChannelHandler> commandHandlerClass) {
        this.factory.setCommandHandler( commandHandlerClass );
        return this;
    }
    
    public StompPipelineFactory getFactory() {
        return this.factory;
    }
    
    private StompPipelineFactory factory;


}
