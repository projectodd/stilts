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

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.projectodd.stilts.stomp.StompException;
import org.projectodd.stilts.stomp.TransactionalAcknowledger;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;
import org.projectodd.stilts.stomp.protocol.StompFrame.Header;
import org.projectodd.stilts.stomp.protocol.StompFrame.Version;
import org.projectodd.stilts.stomp.spi.StompProvider;

public class NackHandler extends AbstractControlFrameHandler {

    public NackHandler(StompProvider server, ConnectionContext context) {
        super( server, context, Command.NACK );
    }

    public void handleControlFrame(ChannelHandlerContext channelContext, StompFrame frame) {
        try {

            Version version = getContext().getStompConnection().getVersion();
            if (version.isBefore( Version.VERSION_1_1 )) {
                throw new StompException( "NACK unsupported prior to STOMP 1.1." );
            }

            String messageId = frame.getHeader( Header.MESSAGE_ID );
            if (StringUtils.isEmpty( messageId )) {
                throw new StompException( "Cannot NACK without message ID." );
            }
            
            String subscription = frame.getHeader( Header.SUBSCRIPTION );
            if (StringUtils.isEmpty( subscription )) {
                throw new StompException( "Cannot NACK without subscription ID." );
            }

            TransactionalAcknowledger acknowledger = getContext().getAckManager().removeAcknowledger( messageId );
            if (acknowledger == null) {
                log.warn( "Attempting to NACK non-existent message ID: " + messageId );
            }
            else {
                String transactionId = frame.getHeader( Header.TRANSACTION );
                acknowledger.nack( transactionId );
            }
        } catch (StompException e) {
            sendError( channelContext, e.getMessage(), frame );
        } catch (Exception e) {
            sendError( channelContext, "Unable to NACK", frame );
        }
    }

    private static Logger log = Logger.getLogger( NackHandler.class );

}
