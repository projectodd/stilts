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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.ssl.SslHandler;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.server.protocol.http.ConnectionManager;
import org.projectodd.stilts.stomp.server.protocol.http.FlashPolicyFileHandler;
import org.projectodd.stilts.stomp.server.protocol.http.SinkManager;
import org.projectodd.stilts.stomp.server.protocol.resource.ResourceManager;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class StompServerPipelineFactory implements ChannelPipelineFactory {

    public StompServerPipelineFactory(StompProvider provider, Executor executor, SSLContext sslContext) {
        this.provider = provider;
        this.executor = executor;
        this.sslContext = sslContext;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
        pipeline.addFirst( "server-debug-header", new DebugHandler( "SERVER-HEAD" ) );
        if (this.sslContext != null) {
            SSLEngine sslEngine = this.sslContext.createSSLEngine();
            sslEngine.setUseClientMode( false );
            SslHandler sslHandler = new SslHandler( sslEngine );
            sslHandler.setEnableRenegotiation( false );
            pipeline.addLast( "ssl", sslHandler );
        }
        pipeline.addLast( "flash-policy-file-handler", new FlashPolicyFileHandler() );
        pipeline.addLast( "protocol-detector", new ProtocolDetector( this.connectionManager, this.sinkManager, this.provider, this.executor, this.resourceManager) );
        return pipeline;
    }

    private ConnectionManager connectionManager = new ConnectionManager();
    private SinkManager sinkManager = new SinkManager();
    private Executor executor;

    private StompProvider provider;
    private SSLContext sslContext;
    private ResourceManager resourceManager = new ResourceManager();;

}
