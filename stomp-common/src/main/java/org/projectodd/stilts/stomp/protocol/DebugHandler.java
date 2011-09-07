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

import java.nio.charset.Charset;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpResponse;

public class DebugHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    public DebugHandler(String scope) {
        this.scope = scope;
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log.debug( scope + " >>outbound>> " + e + " :: " + e.getClass() );
        dump( ">>outbound>>", e );
        ctx.sendDownstream( e );
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log.debug( scope + " <<inbound<< " + e + " :: " + e.getClass() );
        dump( "<<inbound<<", e );
        ctx.sendUpstream( e );
    }

    protected void dump(String direction, ChannelEvent e) {
        if (e instanceof ExceptionEvent) {
            dump( direction, (ExceptionEvent) e );
        } else if (e instanceof MessageEvent) {
            dump( direction, (MessageEvent) e );
        }
    }

    protected void dump(String direction, ExceptionEvent e) {
        log.error( scope + " " + direction + " EXCEPTION " + e.getCause() );
    }

    protected void dump(String direction, MessageEvent e) {
        Object message = e.getMessage();

        if (message instanceof ChannelBuffer) {
            ChannelBuffer buffer = (ChannelBuffer) message;
            log.debug( scope + " " + direction + " MESSAGE+BUFFER " + buffer.toString( Charset.forName( "UTF-8" ) ) );
        } else if (message instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) message;
            log.debug( scope + " " + direction + " MESSAGE+HTTP_RESPONSE " + response );
            log.debug( scope + " " + direction + " MESSAGE+HTTP_RESPONSE+BUFFER " + response.getContent() );
        } else {
            log.debug( scope + " " + direction + " MESSAGE " + message );
        }
    }

    private static final Logger log = Logger.getLogger( DebugHandler.class );
    private String scope;

}
