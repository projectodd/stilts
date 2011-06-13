package org.jboss.stilts.protocol.transport;

import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.jboss.stilts.protocol.StompContentFrame;
import org.jboss.stilts.protocol.StompFrame;
import org.jboss.stilts.protocol.StompFrame.Header;

public class StompFrameEncoder extends OneToOneEncoder {

    private static final int HEADER_ESTIMATE = 1024;

    private static final byte[] HEADER_DELIM = ": ".getBytes();
    private static final byte NEWLINE = (byte) '\n';
    private static final byte NULL = (byte) 0x00;

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof StompFrame) {
            StompFrame frame = (StompFrame) msg;
            ChannelBuffer buffer = newBuffer( frame );
            writeHeader( frame, buffer );
            writeContent( frame, buffer );
        }
        return null;
    }

    protected ChannelBuffer newBuffer(StompFrame frame) {
        if (frame instanceof StompContentFrame) {
            return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE + ((StompContentFrame) frame).getContent().capacity() );
        }

        return ChannelBuffers.dynamicBuffer( HEADER_ESTIMATE );
    }

    protected void writeHeader(StompFrame frame, ChannelBuffer buffer) {
        buffer.writeBytes( frame.getCommand().getBytes() );
        buffer.writeByte( NEWLINE );
        Set<String> headerNames = frame.getHeaderNames();
        for (String name : headerNames) {
            if (name.equalsIgnoreCase( "content-length" )) {
                continue;
            }
            buffer.writeBytes( name.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( frame.getHeader( name ).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        if (frame instanceof StompContentFrame) {
            int length = ((StompContentFrame) frame).getContent().readableBytes();
            buffer.writeBytes( Header.CONTENT_LENGTH.getBytes() );
            buffer.writeBytes( HEADER_DELIM );
            buffer.writeBytes( ("" + length).getBytes() );
            buffer.writeByte( NEWLINE );
        }

        buffer.writeByte( NEWLINE );
    }

    protected void writeContent(StompFrame frame, ChannelBuffer buffer) {
        if (frame instanceof StompContentFrame) {
            ChannelBuffer content = ((StompContentFrame)frame).getContent();
            buffer.writeBytes( content );
        }
        buffer.writeByte( NULL );
        return;
    }

}
