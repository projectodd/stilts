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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.projectodd.stilts.StompMessage;
import org.projectodd.stilts.logging.Logger;
import org.projectodd.stilts.stomp.protocol.StompFrame.Command;

public class StompMessageEncoder extends OneToOneEncoder {
    
    public StompMessageEncoder(Logger log) {
        this.log = log; 
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompFrame ) {
            return msg;
        }
        if (msg instanceof StompMessage) {
            StompMessage message = (StompMessage) msg;
            log.trace(  "encode: " + message );
            FrameHeader header = new FrameHeader( Command.MESSAGE, message.getHeaders() );
            StompContentFrame frame = new StompContentFrame( header, message.getContent() );
            log.trace(  "encode.frame: " + frame );
            return frame;
        }
        return null;
    }

    private Logger log;

}
