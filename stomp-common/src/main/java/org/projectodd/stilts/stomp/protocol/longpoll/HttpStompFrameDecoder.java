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

package org.projectodd.stilts.stomp.protocol.longpoll;

import java.nio.charset.Charset;

import org.jboss.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.projectodd.stilts.stomp.protocol.StompFrame;
import org.projectodd.stilts.stomp.protocol.StompFrameCodec;

public class HttpStompFrameDecoder extends OneToOneDecoder {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );
    private static Logger log = Logger.getLogger( HttpStompFrameDecoder.class );

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof HttpMessage) {
            String contentType = ((HttpMessage) msg).getHeader( "content-type" );
            if (contentType != null) {
                int semiLoc = contentType.indexOf( ";" );
                if (semiLoc > 0) {
                    contentType = contentType.substring( 0, semiLoc );
                }
                contentType = contentType.trim();
                if (contentType.equals( "text/stomp" )) {
                    HttpMessage httpMessage = (HttpMessage) msg;
                    ChannelBuffer buffer = httpMessage.getContent();
                    StompFrame stompFrame = StompFrameCodec.INSTANCE.decode( buffer );
                    return stompFrame;
                }
            }
        }
        return msg;
    }

}
