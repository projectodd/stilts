package org.jboss.stilts.protocol.transport;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;
import org.jboss.stilts.protocol.FrameHeader;
import org.jboss.stilts.protocol.StompContentFrame;
import org.jboss.stilts.protocol.StompControlFrame;
import org.jboss.stilts.protocol.StompFrame.Command;

public class StompFrameDecoder extends ReplayingDecoder<VoidEnum> {

    private static final Charset UTF_8 = Charset.forName( "UTF-8" );

    public static final int DEFAULT_MAX_FRAME_SIZE = 16384;

    public StompFrameDecoder() {
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer, VoidEnum state) throws Exception {
        FrameHeader header = decodeHeader( buffer );

        int len = header.getContentLength();

        ChannelBuffer content = null;

        if (len < 0) {
            content = readUntilNull( buffer );
        } else {
            content = readUntil( buffer, len );
        }

        if (content != null) {
            if (header.isContentFrame()) {
                return new StompContentFrame( header, content );
            } else {
                return new StompControlFrame( header );
            }
        }

        return null;
    }

    protected ChannelBuffer readUntilNull(ChannelBuffer buffer) {
        int nonNullBytes = buffer.bytesBefore( (byte) 0x00 );

        if (nonNullBytes == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }

        ChannelBuffer content = buffer.readBytes( nonNullBytes );
        buffer.readByte();
        return content;
    }

    protected ChannelBuffer readUntil(ChannelBuffer buffer, int len) {
        if (buffer.readableBytes() < (len + 1)) {
            return null;
        }

        ChannelBuffer content = buffer.readBytes( len  );
        buffer.readByte();
        return content;
    }

    protected FrameHeader decodeHeader(ChannelBuffer buffer) {
        FrameHeader header = null;

        while (buffer.readable()) {
            int nonNewLineBytes = buffer.bytesBefore( (byte) '\n' );

            if (nonNewLineBytes == 0) {
                buffer.readByte();
                break;
            }
            if (nonNewLineBytes >= 0) {
                ChannelBuffer line = buffer.readBytes( nonNewLineBytes );
                buffer.readByte();
                header = processHeaderLine( header, line.toString( UTF_8 ) );
            }
        }

        return header;
    }

    protected FrameHeader processHeaderLine(FrameHeader header, String line) {
        if (header == null) {
            header = new FrameHeader();
            header.setCommand( Command.valueOf( line ) );
            return header;
        }

        int colonLoc = line.indexOf( ":" );
        if (colonLoc > 0) {
            String name = line.substring( 0, colonLoc );
            String value = line.substring( colonLoc + 1 );
            header.set( name, value );
        }

        return header;
    }
}
