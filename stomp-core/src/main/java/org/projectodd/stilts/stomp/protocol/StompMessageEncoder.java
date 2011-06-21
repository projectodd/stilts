/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the Apache License 2.0. 
 * 
 * You should have received a copy of the Apache License 
 * along with this software; if not, please see:
 * http://apache.org/licenses/LICENSE-2.0.txt
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
