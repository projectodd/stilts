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

package org.projectodd.stilts.stomp.server.protocol;

import java.util.concurrent.Executor;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompServerPipelineFactory implements ChannelPipelineFactory {

    public StompServerPipelineFactory(StompProvider provider, Executor executor) {
        this.provider = provider;
        this.executor = executor;
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
        pipeline.addFirst( "server-debug-header", new DebugHandler( "SERVER-HEAD" ) );
        pipeline.addLast( "protocol-detector", new ProtocolDetector( this.provider, this.executor ) );
        pipeline.addLast( "server-post-proto", new DebugHandler( "SERVER-POST-PROTO" ) );
        return pipeline;
    }
    
    private Executor executor;
    
    private StompProvider provider;


}
