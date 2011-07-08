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

package org.projectodd.stilts.stomp.protocol.server;

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.spi.StompProvider;

/** Handler for inbound SEND frames from client to server.
 * 
 * @author Bob McWhirter
 */
public class SendHandler extends AbstractProviderHandler {
	
	private static Logger log = Logger.getLogger(SendHandler.class);

    public SendHandler(StompProvider server, ConnectionContext context) {
        super( server, context );
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof StompMessage) {
            StompMessage message = (StompMessage) e.getMessage();
            String transactionId = message.getHeaders().get( Header.TRANSACTION );
            getContext().getStompConnection().send( (StompMessage) e.getMessage(), transactionId );
        }
        super.messageReceived( ctx, e );
    }

}
