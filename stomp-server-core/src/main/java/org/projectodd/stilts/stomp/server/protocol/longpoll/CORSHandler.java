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

import org.jboss.logging.Logger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class CORSHandler extends SimpleChannelUpstreamHandler implements ChannelDownstreamHandler {

    private static Logger log = Logger.getLogger( CORSHandler.class );

    public CORSHandler() {

    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof HttpRequest) {
            HttpRequest httpReq = (HttpRequest) e.getMessage();
            String origin = httpReq.getHeader( "Origin" );
            if (origin != null) {
                this.origin = origin;
            }
        }
        ctx.sendUpstream( e );
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof MessageEvent) {
            if (((MessageEvent) e).getMessage() instanceof HttpResponse) {
                HttpResponse httpResp = (HttpResponse) ((MessageEvent) e).getMessage();
                httpResp.setHeader( "Access-Control-Allow-Origin", this.origin );
                httpResp.setHeader( "Access-Control-Allow-Methods", "OPTIONS,POST,GET" );
                httpResp.setHeader( "Access-Control-Allow-Headers", "content-type" );
                httpResp.setHeader( "Access-Control-Allow-Credentials", "true" );
            }
        }
        ctx.sendDownstream( e );
    }

    private String origin = "*";
}
