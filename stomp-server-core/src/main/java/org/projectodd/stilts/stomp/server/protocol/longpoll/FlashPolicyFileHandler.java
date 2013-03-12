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

package org.projectodd.stilts.stomp.server.protocol.longpoll;

import java.nio.charset.Charset;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class FlashPolicyFileHandler extends SimpleChannelUpstreamHandler {

    private static Logger log = Logger.getLogger( FlashPolicyFileHandler.class );
    private static final String POLICY = "<?xml version=\"1.0\"?>\n<cross-domain-policy>\n  <allow-access-from domain=\"*\" to-ports=\"8675\" />\n</cross-domain-policy>\n";

    public FlashPolicyFileHandler() {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof ChannelBuffer) {
            ChannelBuffer content = (ChannelBuffer) e.getMessage();
            int readable = content.readableBytes();
            if ( readable > 0 && readable < 100 ) {
                byte[] bytes = new byte[ readable ];
                content.getBytes(  content.readerIndex(), bytes );
                String contentStr = new String( bytes );
                log.debug(  contentStr  );
                if ( contentStr.trim().equals( "<policy-file-request/>")) {
                    ChannelBuffer response = ChannelBuffers.copiedBuffer( POLICY, Charset.forName( "UTF-8" ) );
                    ChannelFuture future = ctx.getChannel().write( response );
                    future.addListener( new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            future.getChannel().close();
                        }
                    } );
                    return;
                }
            }
        }
        ctx.sendUpstream( e );
    }
}
