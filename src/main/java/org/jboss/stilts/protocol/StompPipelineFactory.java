package org.jboss.stilts.protocol;

import javax.xml.soap.MessageFactory;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.stilts.logging.Logger;
import org.jboss.stilts.logging.LoggerManager;
import org.jboss.stilts.protocol.server.AbortHandler;
import org.jboss.stilts.protocol.server.BeginHandler;
import org.jboss.stilts.protocol.server.CommitHandler;
import org.jboss.stilts.protocol.server.ConnectHandler;
import org.jboss.stilts.protocol.server.ConnectionContext;
import org.jboss.stilts.protocol.server.DefaultStompMessageFactory;
import org.jboss.stilts.protocol.server.DisconnectHandler;
import org.jboss.stilts.protocol.server.ReceiptHandler;
import org.jboss.stilts.protocol.server.SendHandler;
import org.jboss.stilts.protocol.server.SubscribeHandler;
import org.jboss.stilts.protocol.server.UnsubscribeHandler;
import org.jboss.stilts.spi.StompMessageFactory;
import org.jboss.stilts.spi.StompProvider;

public class StompPipelineFactory implements ChannelPipelineFactory {

    public StompPipelineFactory(StompProvider provider, LoggerManager loggerManager) {
        this.provider = provider;
        this.loggerManager = loggerManager;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
        
        pipeline.addLast( "debug-head", new DebugHandler( log( "DEBUG.head" ) ) );
        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder( log( "frame.encoder") ) );
        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder( log( "frame.decode" ) ) );
        
        ConnectionContext context = new ConnectionContext( this.loggerManager );
        pipeline.addLast( "stomp-server-connect", new ConnectHandler( provider, context ) );
        pipeline.addLast( "stomp-server-disconnect", new DisconnectHandler( provider, context ) );

        pipeline.addLast( "stomp-server-subscribe", new SubscribeHandler( provider, context ) );
        pipeline.addLast( "stomp-server-unsubscribe", new UnsubscribeHandler( provider, context ) );

        pipeline.addLast( "stomp-server-begin", new BeginHandler( provider, context ) );
        pipeline.addLast( "stomp-server-commit", new CommitHandler( provider, context ) );
        pipeline.addLast( "stomp-server-abort", new AbortHandler( provider, context ) );
        
        pipeline.addLast( "stomp-server-receipt", new ReceiptHandler( provider, context ) );
        
        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder( log( "message.encoder") ) );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( log( "message.decode" ), DefaultStompMessageFactory.INSTANCE ) ); 
        
        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );
        pipeline.addLast( "debug-tail", new DebugHandler( log( "DEBUG.tail" ) ) );
        
        return pipeline;
    }
    
    Logger log(String name) {
        return this.loggerManager.getLogger( "pipeline.stomp." + name );
    }

    private StompProvider provider;
    private LoggerManager loggerManager;

}
