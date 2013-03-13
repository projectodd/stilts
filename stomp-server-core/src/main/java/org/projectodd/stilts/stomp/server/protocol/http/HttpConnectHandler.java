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

package org.projectodd.stilts.stomp.server.protocol.http;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.stomp.server.protocol.ConnectHandler;
import org.projectodd.stilts.stomp.server.protocol.WrappedConnectionContext;
import org.projectodd.stilts.stomp.spi.StompProvider;
import org.projectodd.stilts.stomp.spi.TransactionalAcknowledgeableMessageSink;

public class HttpConnectHandler extends ConnectHandler {

    public HttpConnectHandler(StompProvider server, WrappedConnectionContext context, SinkManager sinkManager) {
        super( server, context );
        this.sinkManager = sinkManager;
    }

    protected TransactionalAcknowledgeableMessageSink createMessageSink(ChannelHandlerContext ctx) {
        HttpMessageSink sink = new HttpMessageSink( getContext().getAckManager() );
        sinkManager.put( ((WrappedConnectionContext)getContext()).getConnectionContext(), sink );
        return sink;
    }

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger( HttpConnectHandler.class );
    private SinkManager sinkManager;


}
