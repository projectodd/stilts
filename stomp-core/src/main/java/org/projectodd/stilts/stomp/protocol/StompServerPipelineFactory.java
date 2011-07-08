/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.stomp.protocol;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.execution.ExecutionHandler;
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
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompServerPipelineFactory implements ChannelPipelineFactory {

    public StompServerPipelineFactory(StompProvider provider, Executor executor) {
        this.provider = provider;
        this.executor = executor;
        if ( this.executor != null ) {
            this.executionHandler = new ExecutionHandler( this.executor );
        }
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
        
        //pipeline.addLast( "debug-head", new DebugHandler( log( "DEBUG.head" ) ) );
        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder( ));
        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder( ));
        
        ConnectionContext context = new ConnectionContext( );
        pipeline.addLast( "stomp-server-connect", new ConnectHandler( provider, context ) );
        pipeline.addLast( "stomp-server-disconnect", new DisconnectHandler( provider, context ) );

        pipeline.addLast( "stomp-server-begin", new BeginHandler( provider, context ) );
        pipeline.addLast( "stomp-server-commit", new CommitHandler( provider, context ) );
        pipeline.addLast( "stomp-server-abort", new AbortHandler( provider, context ) );
        
        pipeline.addLast( "stomp-server-ack", new AckHandler( provider, context ) );
        pipeline.addLast( "stomp-server-nack", new NackHandler( provider, context ) );
        
        pipeline.addLast( "stomp-server-receipt", new ReceiptHandler( provider, context ) );
        
        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder( ));
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( DefaultStompMessageFactory.INSTANCE ) ); 
        
        if ( this.executionHandler != null ) {
            pipeline.addLast( "stomp-server-send-threading", this.executionHandler );
        }
        
        pipeline.addLast( "stomp-server-send", new SendHandler( provider, context ) );
        //pipeline.addLast( "debug-tail", new DebugHandler( log( "DEBUG.tail" ) ) );
        
        return pipeline;
    }
    
    private Executor executor;
    private ExecutionHandler executionHandler;
    
    private StompProvider provider;


}
