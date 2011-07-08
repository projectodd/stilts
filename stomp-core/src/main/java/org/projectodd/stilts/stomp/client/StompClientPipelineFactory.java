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

package org.projectodd.stilts.stomp.client;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.stomp.protocol.DebugHandler;
import org.projectodd.stilts.stomp.protocol.StompFrameDecoder;
import org.projectodd.stilts.stomp.protocol.StompFrameEncoder;
import org.projectodd.stilts.stomp.protocol.StompMessageDecoder;
import org.projectodd.stilts.stomp.protocol.StompMessageEncoder;
import org.projectodd.stilts.stomp.protocol.client.ClientContext;
import org.projectodd.stilts.stomp.protocol.client.ClientMessageHandler;
import org.projectodd.stilts.stomp.protocol.client.ClientReceiptHandler;
import org.projectodd.stilts.stomp.protocol.client.ConnectedHandler;

public class StompClientPipelineFactory implements ChannelPipelineFactory {

    public StompClientPipelineFactory(SimpleStompClient client, ClientContext clientContext) {
        this.client = client;
        this.clientContext = clientContext;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast( "debug-head", new DebugHandler( log( "DEBUG.head" ) ) );

        pipeline.addLast( "stomp-frame-decoder", new StompFrameDecoder( log( "frame.decoder" ) ) );
        pipeline.addLast( "stomp-frame-encoder", new StompFrameEncoder( log( "frame.encoder" ) ) );
        pipeline.addLast( "debug-frame-encoders", new DebugHandler( log( "DEBUG.frame-encoders" ) ) );

        pipeline.addLast( "stomp-client-connect", new ConnectedHandler( clientContext ) );
        pipeline.addLast( "stomp-client-receipt", new ClientReceiptHandler( clientContext ) );

        pipeline.addLast( "stomp-message-encoder", new StompMessageEncoder( log( "message.encoder" ) ) );
        pipeline.addLast( "stomp-message-decoder", new StompMessageDecoder( log( "message.decoder" ), new ClientStompMessageFactory( this.client ) ) );
        pipeline.addLast( "debug-message-encoders", new DebugHandler( log( "DEBUG.message-encoders" ) ) );

        pipeline.addLast( "stomp-client-message-handler", new ClientMessageHandler( clientContext ) );

        return pipeline;
    }

    Logger log(String name) {
        return this.clientContext.getLoggerManager().getLogger( "pipeline.stomp." + name );
    }

    private SimpleStompClient client;
    private ClientContext clientContext;

}
