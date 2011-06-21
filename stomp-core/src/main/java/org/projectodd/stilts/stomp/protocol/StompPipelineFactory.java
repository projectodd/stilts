/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
 */

package org.projectodd.stilts.stomp.protocol;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.logging.LoggerManager;
import org.projectodd.stilts.stomp.protocol.server.AbortHandler;
import org.projectodd.stilts.stomp.protocol.server.AckHandler;
import org.projectodd.stilts.stomp.protocol.server.BeginHandler;
import org.projectodd.stilts.stomp.protocol.server.CommitHandler;
import org.projectodd.stilts.stomp.protocol.server.ConnectHandler;
import org.projectodd.stilts.stomp.protocol.server.ConnectionContext;
import org.projectodd.stilts.stomp.protocol.server.DefaultStompMessageFactory;
import org.projectodd.stilts.stomp.protocol.server.DisconnectHandler;
import org.projectodd.stilts.stomp.protocol.server.NackHandler;
import org.projectodd.stilts.stomp.protocol.server.ReceiptHandler;
import org.projectodd.stilts.stomp.protocol.server.SendHandler;
import org.projectodd.stilts.stomp.protocol.server.SubscribeHandler;
import org.projectodd.stilts.stomp.protocol.server.UnsubscribeHandler;
import org.projectodd.stilts.stomp.spi.StompProvider;

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
        
        pipeline.addLast( "stomp-server-ack", new AckHandler( provider, context ) );
        pipeline.addLast( "stomp-server-nack", new NackHandler( provider, context ) );
        
        pipeline.addLast( "stomp-server-receipt", new ReceiptHandler( provider, context ) );
        
        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder( log( "message.encoder") ) );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( log( "message.decode" ), DefaultStompMessageFactory.INSTANCE ) ); 
        
        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );
        pipeline.addLast( "debug-tail", new DebugHandler( log( "DEBUG.tail" ) ) );
        
        return pipeline;
    }
    
    Logger log(String name) {
    	System.err.println( "MANAGER: " + this.loggerManager );
        return this.loggerManager.getLogger( "pipeline.stomp." + name );
    }

    private StompProvider provider;
    private LoggerManager loggerManager;

}
